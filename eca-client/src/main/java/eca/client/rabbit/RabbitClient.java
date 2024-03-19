package eca.client.rabbit;

import com.rabbitmq.client.AMQP;
import eca.client.adapter.ClassifierOptionsAdapter;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.client.dto.options.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.AbstractClassifier;

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

    private final ClassifierOptionsAdapter classifierOptionsAdapter = new ClassifierOptionsAdapter();

    /**
     * Sends evaluation request.
     *
     * @param classifier    - classifier
     * @param dataUuid      - training data uuid from central data storage
     * @param replyTo       - reply to queue name
     * @param correlationId - correlation id
     */
    public void sendEvaluationRequest(AbstractClassifier classifier, String dataUuid, String replyTo,
                                      String correlationId) {
        Objects.requireNonNull(classifier, "Classifier must be specified!");
        Objects.requireNonNull(dataUuid, "Data uuid must be specified!");
        log.info("Starting to send request with correlation id [{}] to eca - service for model '{}', data uuid '{}'.",
                correlationId, classifier.getClass().getSimpleName(), dataUuid);
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequest(classifier, dataUuid);
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(evaluationRequestQueue, evaluationRequestDto, basicProperties);
        log.info("Request with correlation id [{}] has been sent for model '{}', data uuid '{}'.",
                correlationId, classifier.getClass().getSimpleName(), dataUuid);
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
        log.info(
                "Starting to send request with correlation id [{}] to eca - service for experiment '{}', data uuid '{}'.",
                correlationId, experimentRequestDto.getExperimentType(), experimentRequestDto.getDataUuid());
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(experimentRequestQueue, experimentRequestDto, basicProperties);
        log.info("Request with correlation id [{}] has been sent for experiment {}.", correlationId,
                experimentRequestDto.getExperimentType());
    }

    /**
     * Sends evaluation request.
     *
     * @param dataUuid          - training data uuid from central data storage
     * @param replyTo       - reply to queue name
     * @param correlationId - correlation id
     */
    public void sendEvaluationRequest(String dataUuid, String replyTo, String correlationId) {
        Objects.requireNonNull(dataUuid, "Instances must be specified!");
        log.info("Starting to send evaluation request with correlation id [{}] to eca - service for data uuid '{}'.",
                correlationId, dataUuid);
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(evaluationOptimizerRequestQueue, new InstancesRequest(dataUuid), basicProperties);
        log.info("Evaluation request with correlation id [{}] has been sent to eca - service for data uuid '{}'.",
                correlationId, dataUuid);
    }

    private EvaluationRequestDto createEvaluationRequest(AbstractClassifier classifier, String dataUuid) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        ClassifierOptions classifierOptions = classifierOptionsAdapter.convert(classifier);
        evaluationRequestDto.setClassifierOptions(classifierOptions);
        evaluationRequestDto.setDataUuid(dataUuid);
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDto.setNumFolds(numFolds);
            evaluationRequestDto.setNumTests(numTests);
            evaluationRequestDto.setSeed(seed);
        }
        return evaluationRequestDto;
    }
}
