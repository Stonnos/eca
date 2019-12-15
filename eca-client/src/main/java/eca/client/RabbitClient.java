package eca.client;

import eca.client.dto.ExperimentRequestDto;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Implements Rabbit MQ client for communication with eca - service API.
 *
 * @author Roman Batygin
 */
public interface RabbitClient {

    /**
     * Sends request to eca - service for classifier model evaluation.
     *
     * @param classifier    - classifier object
     * @param data          - training data object
     * @param replyTo       - reply to header
     * @param correlationId - correlation id header
     */
    void sendEvaluationRequest(AbstractClassifier classifier, Instances data, String replyTo, String correlationId);

    /**
     * Sends experiment request to eca - service.
     *
     * @param experimentRequestDto - experiment request dto object
     * @param replyTo              - reply to header
     * @param correlationId        - correlation id header
     */
    void sendExperimentRequest(ExperimentRequestDto experimentRequestDto, String replyTo, String correlationId);

    /**
     * Sends request to eca - service for classifier evaluation with optimal options.
     *
     * @param data          - training data
     * @param replyTo       - reply to header
     * @param correlationId - correlation id header
     */
    void sendEvaluationRequest(Instances data, String replyTo, String correlationId);

}
