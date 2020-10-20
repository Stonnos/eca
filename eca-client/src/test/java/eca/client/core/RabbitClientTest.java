package eca.client.core;

import com.rabbitmq.client.AMQP;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.client.util.Queues;
import eca.core.evaluation.EvaluationMethod;
import eca.trees.CART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.util.UUID;

import static eca.client.TestHelperUtils.createEvaluationRequestDto;
import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static eca.client.TestHelperUtils.loadInstances;
import static eca.client.util.RabbitUtils.buildMessageProperties;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    public static final int NUM_TESTS = 1;
    public static final int NUM_FOLDS = 10;
    public static final int SEED = 1;

    private RabbitSender rabbitSender;

    private RabbitClient rabbitClient;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances();
        rabbitSender = mock(RabbitSender.class);
        rabbitClient = new RabbitClient(rabbitSender);
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
        experimentRequestDto.setData(instances);
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        rabbitClient.sendExperimentRequest(experimentRequestDto, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(Queues.EXPERIMENT_REQUEST_QUEUE, experimentRequestDto,
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
        expectedRequest.setData(instances);
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        rabbitClient.sendEvaluationRequest(instances, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(Queues.EVALUATION_OPTIMIZER_REQUEST_QUEUE, expectedRequest,
                expectedProperties);
    }

    @Test
    void testEvaluationRequestWithNullClassifier() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendEvaluationRequest(null, instances, REPLY_TO, UUID.randomUUID().toString()));
    }

    @Test
    void testEvaluationRequestWithNullData() {
        assertThrows(NullPointerException.class,
                () -> rabbitClient.sendEvaluationRequest(new CART(), null, REPLY_TO, UUID.randomUUID().toString()));
    }

    @Test
    void testSendEvaluationRequestWithTrainingDataEvaluationMethod() {
        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        CART cart = new CART();
        EvaluationRequestDto expectedRequest =
                createEvaluationRequestDto(cart, instances, EvaluationMethod.TRAINING_DATA);
        rabbitClient.sendEvaluationRequest(cart, instances, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(Queues.EVALUATION_REQUEST_QUEUE, expectedRequest,
                expectedProperties);
    }

    @Test
    void testSendEvaluationRequestWithCrossValidationMethod() {
        String correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties expectedProperties = buildMessageProperties(REPLY_TO, correlationId);
        CART cart = new CART();
        EvaluationRequestDto expectedRequest =
                createEvaluationRequestDto(cart, instances, EvaluationMethod.CROSS_VALIDATION);
        expectedRequest.setNumFolds(NUM_FOLDS);
        expectedRequest.setNumTests(NUM_TESTS);
        expectedRequest.setSeed(SEED);
        rabbitClient.setEvaluationMethod(EvaluationMethod.CROSS_VALIDATION);
        rabbitClient.setNumFolds(NUM_FOLDS);
        rabbitClient.setNumTests(NUM_TESTS);
        rabbitClient.setSeed(SEED);
        rabbitClient.sendEvaluationRequest(cart, instances, REPLY_TO, correlationId);
        verify(rabbitSender, atLeastOnce()).sendMessage(Queues.EVALUATION_REQUEST_QUEUE, expectedRequest,
                expectedProperties);
    }
}
