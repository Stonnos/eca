package eca.client.rabbit;

import com.rabbitmq.client.AMQP;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.Objects;

import static eca.client.util.RabbitUtils.buildMessageProperties;

/**
 * Implements service for communication with eca - service API.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class RabbitClient {

    /**
     * Rabbit sender
     */
    @Getter
    private final RabbitSender rabbitSender;

    /**
     * Evaluation test method
     **/
    @Getter
    @Setter
    private EvaluationMethod evaluationMethod = EvaluationMethod.TRAINING_DATA;

    /**
     * Number of folds
     **/
    @Getter
    @Setter
    private Integer numFolds;

    /**
     * Number of tests
     **/
    @Getter
    @Setter
    private Integer numTests;

    /**
     * Seed value for random generator
     */
    @Getter
    @Setter
    private Integer seed;

    /**
     * Evaluation request queue.
     */
    @Getter
    @Setter
    private String evaluationRequestQueue;

    /**
     * Evaluation optimizer request queue.
     */
    @Getter
    @Setter
    private String evaluationOptimizerRequestQueue;

    /**
     * Experiment request queue.
     */
    @Getter
    @Setter
    private String experimentRequestQueue;

    /**
     * Sends evaluation request.
     *
     * @param classifier    - classifier
     * @param data          - training data
     * @param replyTo       - reply to queue name
     * @param correlationId - correlation id
     */
    public void sendEvaluationRequest(AbstractClassifier classifier, Instances data, String replyTo,
                                      String correlationId) {
        Objects.requireNonNull(classifier, "Classifier must be specified!");
        Objects.requireNonNull(data, "Instances must be specified!");
        log.info("Starting to send request with correlation id [{}] to eca - service for model '{}', data '{}'.",
                correlationId, classifier.getClass().getSimpleName(), data.relationName());
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequest(classifier, data);
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(evaluationRequestQueue, evaluationRequestDto, basicProperties);
        log.info("Request with correlation id [{}] has been sent for model '{}', data '{}'.",
                correlationId, classifier.getClass().getSimpleName(), data.relationName());
    }

    /**
     * Sends experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @param replyTo              - reply to queue name
     * @param correlationId        - correlation id
     */
    public void sendExperimentRequest(ExperimentRequestDto experimentRequestDto, String replyTo, String correlationId) {
        Objects.requireNonNull(experimentRequestDto, "Experiment request is not specified!");
        log.info("Starting to send request with correlation id [{}] to eca - service for experiment '{}', data '{}'.",
                correlationId, experimentRequestDto.getExperimentType(), experimentRequestDto.getData().relationName());
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(experimentRequestQueue, experimentRequestDto, basicProperties);
        log.info("Request with correlation id [{}] has been sent for experiment {}.", correlationId,
                experimentRequestDto.getExperimentType());
    }

    /**
     * Sends evaluation request.
     *
     * @param data          - training data
     * @param replyTo       - reply to queue name
     * @param correlationId - correlation id
     */
    public void sendEvaluationRequest(Instances data, String replyTo, String correlationId) {
        Objects.requireNonNull(data, "Instances must be specified!");
        log.info("Starting to send evaluation request with correlation id [{}] to eca - service for data '{}'.",
                correlationId, data.relationName());
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(evaluationOptimizerRequestQueue, new InstancesRequest("", data), basicProperties);
        log.info("Evaluation request with correlation id [{}] has been sent to eca - service for data '{}'.",
                correlationId, data.relationName());
    }

    private EvaluationRequestDto createEvaluationRequest(AbstractClassifier classifier, Instances data) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(classifier);
        evaluationRequestDto.setData(data);
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDto.setNumFolds(numFolds);
            evaluationRequestDto.setNumTests(numTests);
            evaluationRequestDto.setSeed(seed);
        }
        return evaluationRequestDto;
    }
}
