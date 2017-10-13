package eca.client;

import eca.client.dto.ExperimentRequestDto;
import eca.core.evaluation.EvaluationResults;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Implements service for communication with eca - service api.
 *
 * @author Roman Batygin
 */
public interface RestClient {

    /**
     * Sends request to eca - service.
     *
     * @param classifier {@link AbstractClassifier} object
     * @param data {@link Instances} object
     * @return {@link EvaluationResults} object
     */
    EvaluationResults performRequest(AbstractClassifier classifier, Instances data);

    /**
     * Sends experiment request to eca - service.
     * @param experimentRequestDto {@link ExperimentRequestDto} object
     */
    void createExperimentRequest(ExperimentRequestDto experimentRequestDto);

}
