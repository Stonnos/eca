package eca.client;

import com.rabbitmq.client.ConnectionFactory;
import eca.client.converter.JsonMessageConverter;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.ExperimentResponse;
import eca.client.dto.TechnicalStatus;
import eca.client.instances.UploadInstancesCacheService;
import eca.client.listener.MessageListenerContainer;
import eca.client.listener.adapter.EvaluationListenerAdapter;
import eca.client.listener.adapter.ExperimentListenerAdapter;
import eca.client.rabbit.ConnectionManager;
import eca.client.rabbit.RabbitClient;
import eca.client.rabbit.RabbitSender;
import eca.core.InstancesDataModel;
import eca.core.ModelSerializationHelper;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.UrlResource;
import eca.dataminer.AbstractExperiment;
import eca.trees.CART;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static eca.client.TestHelperUtils.loadInstances;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for eca client api.
 *
 * @author Roman Batygin
 */
@Slf4j
class EcaClientIT {

    private static final long EVALUATION_REQUEST_TIMEOUT_SECONDS = 5L;
    private static final long EXPERIMENT_REQUEST_TIMEOUT_SECONDS = 60L;
    private static final long CONNECTION_TIMEOUT_MINUTES = 1L;
    private static final long POLL_INTERVAL_SECONDS = 1L;
    private static final String ZIP = "zip";

    private final EcaClientTestConfiguration ecaClientConfiguration = EcaClientTestConfiguration.getInstance();

    private final MessageListenerContainer messageListenerContainer = new MessageListenerContainer();

    private final JsonMessageConverter messageConverter = new JsonMessageConverter();

    private RabbitClient rabbitClient;
    private UploadInstancesCacheService uploadInstancesCacheService;

    private String evaluationReplyTo;
    private String experimentReplyTo;

    private Instances instances;

    private EvaluationResponse evaluationResponse;
    private ExperimentResponse experimentResponse;
    private String actualCorrelationId;

    private String expectedCorrelationId;

    private volatile boolean responseReceived;

    @BeforeEach
    void setup() {
        instances = loadInstances();
        responseReceived = false;
        evaluationReplyTo = UUID.randomUUID().toString();
        experimentReplyTo = UUID.randomUUID().toString();
        expectedCorrelationId = UUID.randomUUID().toString();
        ConnectionFactory connectionFactory = ecaClientConfiguration.getConnectionFactory();
        RabbitSender rabbitSender = new RabbitSender();
        rabbitSender.setMessageConverter(messageConverter);
        rabbitSender.setConnectionManager(new ConnectionManager(connectionFactory));
        rabbitClient = new RabbitClient(rabbitSender);
        rabbitClient.setEvaluationRequestQueue(EcaClientTestConfiguration.getEvaluationRequestQueue());
        rabbitClient.setExperimentRequestQueue(EcaClientTestConfiguration.getExperimentRequestQueue());
        uploadInstancesCacheService = ecaClientConfiguration.createUploadInstancesCacheService();
        startContainer(connectionFactory);
    }

    @Test
    void testSendEvaluationRequest() throws IOException {
        String dataUuid = uploadInstances();
        CART cart = new CART();
        rabbitClient.sendEvaluationRequest(cart, dataUuid, evaluationReplyTo, expectedCorrelationId);
        await().timeout(Duration.ofSeconds(EVALUATION_REQUEST_TIMEOUT_SECONDS))
                .until(() -> responseReceived);
        assertEquals(expectedCorrelationId, actualCorrelationId);
        assertNotNull(evaluationResponse);
        assertNotNull(evaluationResponse.getModelUrl());
        assertEquals(TechnicalStatus.SUCCESS, evaluationResponse.getStatus());
        ClassificationModel classificationModel =
                downloadModel(evaluationResponse.getModelUrl(), ClassificationModel.class);
        assertNotNull(classificationModel);
        assertEquals(cart.getClass().getSimpleName(), classificationModel.getClassifier().getClass().getSimpleName());
    }

    @Test
    void testSendExperimentRequest() throws IOException {
        String dataUuid = uploadInstances();
        ExperimentRequestDto experimentRequestDto = createExperimentRequestDto();
        experimentRequestDto.setDataUuid(dataUuid);
        rabbitClient.sendExperimentRequest(experimentRequestDto, experimentReplyTo, expectedCorrelationId);
        await().timeout(Duration.ofSeconds(EXPERIMENT_REQUEST_TIMEOUT_SECONDS))
                .until(() -> responseReceived);
        assertEquals(expectedCorrelationId, actualCorrelationId);
        assertNotNull(experimentResponse);
        assertNotNull(experimentResponse.getDownloadUrl());
        assertEquals(TechnicalStatus.SUCCESS, experimentResponse.getStatus());
        AbstractExperiment<?> abstractExperiment =
                downloadModel(experimentResponse.getDownloadUrl(), AbstractExperiment.class);
        assertNotNull(abstractExperiment);
    }

    @AfterEach
    void stop() throws IOException, TimeoutException {
        messageListenerContainer.stop();
        rabbitClient.getRabbitSender().getConnectionManager().close();
    }

    private <T> T downloadModel(String url, Class<T> modelClazz) throws IOException {
        URL modelUrl = new URL(url);
        UrlResource urlResource = new UrlResource(modelUrl);
        return ModelSerializationHelper.deserialize(urlResource, modelClazz);
    }

    private void startContainer(ConnectionFactory connectionFactory) {
        messageListenerContainer.setConnectionFactory(connectionFactory);
        EvaluationListenerAdapter evaluationListenerAdapter =
                new EvaluationListenerAdapter(messageConverter, (evaluationResponse, properties) -> {
                    this.actualCorrelationId = properties.getCorrelationId();
                    this.evaluationResponse = evaluationResponse;
                    this.responseReceived = true;
                });
        messageListenerContainer.getRabbitListenerAdapters().put(evaluationReplyTo, evaluationListenerAdapter);

        ExperimentListenerAdapter experimentListenerAdapter =
                new ExperimentListenerAdapter(messageConverter, (experimentResponse, properties) -> {
                    this.actualCorrelationId = properties.getCorrelationId();
                    if (TechnicalStatus.IN_PROGRESS.equals(experimentResponse.getStatus())) {
                        log.info("Experiment request [{}] has been created for correlation id [{}]",
                                experimentResponse.getRequestId(), properties.getCorrelationId());
                    } else {
                        log.info("Received experiment [{}] response with status [{}] for correlation id [{}]",
                                experimentResponse.getRequestId(), experimentResponse.getStatus(),
                                properties.getCorrelationId());
                        this.experimentResponse = experimentResponse;
                        this.responseReceived = true;
                    }
                });
        messageListenerContainer.getRabbitListenerAdapters().put(experimentReplyTo, experimentListenerAdapter);

        messageListenerContainer.start();
        await().timeout(Duration.ofMinutes(CONNECTION_TIMEOUT_MINUTES))
                .pollInterval(Duration.ofSeconds(POLL_INTERVAL_SECONDS))
                .until(messageListenerContainer::isStarted);
    }

    private String uploadInstances() {
        InstancesDataModel instancesDataModel = InstancesDataModel.builder()
                .uuid(UUID.randomUUID().toString())
                .data(instances)
                .build();
        return uploadInstancesCacheService.uploadInstances(instancesDataModel);
    }
}
