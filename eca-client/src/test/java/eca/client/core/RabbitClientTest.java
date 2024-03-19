package eca.client.core;

import com.rabbitmq.client.AMQP;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.client.rabbit.RabbitClient;
import eca.client.rabbit.RabbitSender;
import eca.trees.CART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static eca.client.util.RabbitUtils.buildMessageProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link RabbitClient} class.
 *
 * @author Roman Batygin
 */
class RabbitClientTest {

    private static final String REPLY_TO = "response-queue";
    private static final int NUM_TESTS = 1;
    private static final int NUM_FOLDS = 10;
    private static final int SEED = 1;

    private static final String EXPERIMENT_REQUEST_QUEUE = "experiment-request-queue";
    private static final String EVALUATION_OPTIMIZER_REQUEST_QUEUE = "evaluation-optimizer-request-queue";
    private static final String EVALUATION_REQUEST_QUEUE = "evaluation-request-queue";

    private RabbitSender rabbitSender;

    private RabbitClient rabbitClient;

    @BeforeEach
    void init() {
        rabbitSender = mock(RabbitSender.class);
        rabbitClient = new RabbitClient(rabbitSender);
        rabbitClient.setEvaluationRequestQueue(EVALUATION_REQUEST_QUEUE);
        rabbitClient.setEvaluationOptimizerRequestQueue(EVALUATION_OPTIMIZER_REQUEST_QUEUE);
        rabbitClient.setExperimentRequestQueue(EXPERIMENT_REQUEST_QUEUE);
    }

    @Test
    void testNullExperimentRequest() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendExperimentRequest(null, REPLY_TO, UUID.randomUUID().toString()));
    }

    @Test
    void testSendExperimentRequest() {
        String correlationId = UUID.randomUUID().toString();
        ExperimentRequestDto experimentRequestDto = createExperimentRequestDto();
        experimentRequestDto.setDataUuid(UUID.randomUUID().toString());
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        rabbitClient.sendExperimentRequest(experimentRequestDto, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(EXPERIMENT_REQUEST_QUEUE, experimentRequestDto,
                expectedProperties);
    }

    @Test
    void testNullInstancesRequest() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendEvaluationRequest(null, REPLY_TO, UUID.randomUUID().toString()));
    }

    @Test
    void testSendOptimalClassifierRequest() {
        String correlationId = UUID.randomUUID().toString();
        InstancesRequest expectedRequest = new InstancesRequest();
        expectedRequest.setDataUuid(UUID.randomUUID().toString());
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        rabbitClient.sendEvaluationRequest(expectedRequest.getDataUuid(), REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(EVALUATION_OPTIMIZER_REQUEST_QUEUE, expectedRequest,
                expectedProperties);
    }

    @Test
    void testEvaluationRequestWithNullClassifier() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendEvaluationRequest(null, UUID.randomUUID().toString(), REPLY_TO,
                        UUID.randomUUID().toString()));
    }

    @Test
    void testEvaluationRequestWithNullData() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendEvaluationRequest(new CART(), null, REPLY_TO, UUID.randomUUID().toString()));
    }

    @Test
    void testSendEvaluationRequest() {
        ArgumentCaptor<String> queueNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AMQP.BasicProperties> mqProperties = ArgumentCaptor.forClass(AMQP.BasicProperties.class);
        String correlationId = UUID.randomUUID().toString();
        String dataUuid = UUID.randomUUID().toString();
        CART cart = new CART();
        rabbitClient.sendEvaluationRequest(cart, dataUuid, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(queueNameCaptor.capture(), any(EvaluationRequestDto.class),
                mqProperties.capture());
        assertEquals(queueNameCaptor.getValue(), EVALUATION_REQUEST_QUEUE);
        assertEquals(mqProperties.getValue().getCorrelationId(), correlationId);
        assertEquals(mqProperties.getValue().getReplyTo(), REPLY_TO);
    }
}
