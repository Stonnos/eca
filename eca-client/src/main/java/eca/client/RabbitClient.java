package eca.client;

import com.rabbitmq.client.AMQP;
import eca.client.dto.EvaluationOption;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.client.util.Queues;
import eca.core.evaluation.EvaluationMethod;
import eca.util.Utils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.EnumMap;

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

    public void sendEvaluationRequest(AbstractClassifier classifier, Instances data, String replyTo,
                                      String correlationId) {
        Assert.notNull(classifier, "Classifier must be specified!");
        Assert.notNull(data, "Instances must be specified!");
        log.info("Starting to send request into eca - service for model '{}', data '{}'.",
                classifier.getClass().getSimpleName(), data.relationName());
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequest(classifier, data);
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(Queues.EVALUATION_REQUEST_QUEUE, evaluationRequestDto, basicProperties);
        log.info("Request has been sent for model '{}', data '{}'.", classifier.getClass().getSimpleName(),
                data.relationName());
    }

    public void sendExperimentRequest(ExperimentRequestDto experimentRequestDto, String replyTo, String correlationId) {
        Assert.notNull(experimentRequestDto, "Experiment request is not specified!");
        log.info("Starting to send request into eca - service for experiment '{}', data '{}'.",
                experimentRequestDto.getExperimentType(), experimentRequestDto.getData().relationName());
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(Queues.EXPERIMENT_REQUEST_QUEUE, experimentRequestDto, basicProperties);
        log.info("Request has been sent for experiment {}.", experimentRequestDto.getExperimentType());
    }

    public void sendEvaluationRequest(Instances data, String replyTo, String correlationId) {
        Assert.notNull(data, "Instances must be specified!");
        log.info("Starting to send evaluation request into eca - service for data '{}'.", data.relationName());
        AMQP.BasicProperties basicProperties = buildMessageProperties(replyTo, correlationId);
        rabbitSender.sendMessage(Queues.EVALUATION_OPTIMIZER_REQUEST_QUEUE, new InstancesRequest(data),
                basicProperties);
        log.info("Evaluation request has been sent to eca - service for data '{}'.", data.relationName());
    }

    private EvaluationRequestDto createEvaluationRequest(AbstractClassifier classifier, Instances data) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(classifier);
        evaluationRequestDto.setData(data);
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDto.setEvaluationOptionsMap(new EnumMap<>(EvaluationOption.class));
            Utils.putValueIfNotNull(evaluationRequestDto.getEvaluationOptionsMap(),
                    EvaluationOption.NUM_FOLDS, numFolds);
            Utils.putValueIfNotNull(evaluationRequestDto.getEvaluationOptionsMap(),
                    EvaluationOption.NUM_TESTS, numTests);
            Utils.putValueIfNotNull(evaluationRequestDto.getEvaluationOptionsMap(),
                    EvaluationOption.SEED, seed);
        }
        return evaluationRequestDto;
    }
}