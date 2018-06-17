package eca.client;

import eca.client.dto.EcaResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.core.evaluation.EvaluationResults;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Implements client for communication with eca - service API.
 *
 * @author Roman Batygin
 */
public interface EcaServiceClient {

    /**
     * Sends request to eca - service for classifier model evaluation.
     *
     * @param classifier - classifier object
     * @param data       - training data object
     * @return evaluation results
     */
    EvaluationResults performRequest(AbstractClassifier classifier, Instances data);

    /**
     * Sends experiment request to eca - service.
     *
     * @param experimentRequestDto - experiment request dto object
     */
    EcaResponse createExperimentRequest(ExperimentRequestDto experimentRequestDto);

    /**
     * Sends request to eca - service for classifier evaluation with optimal options.
     *
     * @param data - training data
     * @return evaluation results
     */
    EvaluationResults performRequest(Instances data);

}
