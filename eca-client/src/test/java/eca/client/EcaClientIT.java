package eca.client;

import com.rabbitmq.client.ConnectionFactory;
import eca.client.converter.JsonMessageConverter;
import eca.client.core.ConnectionManager;
import eca.client.core.RabbitClient;
import eca.client.core.RabbitSender;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.TechnicalStatus;
import eca.client.listener.MessageListenerContainer;
import eca.client.listener.adapter.EvaluationListenerAdapter;
import eca.trees.CART;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static eca.client.TestHelperUtils.loadInstances;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration test for eca client api.
 *
 * @author Roman Batygin
 */
class EcaClientIT {

    private static final long REQUEST_TIMEOUT_SECONDS = 5L;
    private static final long CONNECTION_TIMEOUT_MINUTES = 1L;
    private static final long POLL_INTERVAL_SECONDS = 1L;

    private final EcaClientTestConfiguration ecaClientConfiguration = EcaClientTestConfiguration.getInstance();

    private final MessageListenerContainer messageListenerContainer = new MessageListenerContainer();

    private final JsonMessageConverter messageConverter = new JsonMessageConverter();

    private RabbitClient rabbitClient;

    private String replyTo;

    private EvaluationResponse evaluationResponse;
    private String actualCorrelationId;

    private String expectedCorrelationId;

    private volatile boolean responseReceived;

    @BeforeEach
    void setup() {
        responseReceived = false;
        replyTo = UUID.randomUUID().toString();
        expectedCorrelationId = UUID.randomUUID().toString();
        ConnectionFactory connectionFactory = ecaClientConfiguration.getConnectionFactory();

        RabbitSender rabbitSender = new RabbitSender();
        rabbitSender.setMessageConverter(messageConverter);
        rabbitSender.setConnectionManager(new ConnectionManager(connectionFactory));
        rabbitClient = new RabbitClient(rabbitSender);

        startContainer(connectionFactory);
    }

    @Test
    void testSendEvaluationRequest() {
        Instances instances = loadInstances();
        CART cart = new CART();
        rabbitClient.sendEvaluationRequest(cart, instances, replyTo, expectedCorrelationId);
        await().timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .until(() -> responseReceived);
        assertEquals(expectedCorrelationId, actualCorrelationId);
        assertNotNull(evaluationResponse);
        assertNotNull(evaluationResponse.getEvaluationResults());
        assertEquals(TechnicalStatus.SUCCESS, evaluationResponse.getStatus());
    }

    @AfterEach
    void stop() throws IOException, TimeoutException {
        responseReceived = false;
        messageListenerContainer.stop();
    }

    private void startContainer(ConnectionFactory connectionFactory) {
        messageListenerContainer.setConnectionFactory(connectionFactory);
        EvaluationListenerAdapter evaluationListenerAdapter =
                new EvaluationListenerAdapter(messageConverter, (evaluationResponse, properties) -> {
                    this.actualCorrelationId = properties.getCorrelationId();
                    this.evaluationResponse = evaluationResponse;
                    this.responseReceived = true;
                });
        messageListenerContainer.getRabbitListenerAdapters().put(replyTo, evaluationListenerAdapter);

        messageListenerContainer.start();
        await().timeout(Duration.ofMinutes(CONNECTION_TIMEOUT_MINUTES))
                .pollInterval(Duration.ofSeconds(POLL_INTERVAL_SECONDS))
                .until(messageListenerContainer::isStarted);
    }
}
