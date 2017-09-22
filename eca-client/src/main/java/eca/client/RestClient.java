package eca.client;

import eca.core.evaluation.EvaluationResults;
import eca.model.InputData;

/**
 * Implements service for communication with eca - service api.
 *
 * @author Roman Batygin
 */
public interface RestClient {

    /**
     * Sends request to eca - service.
     *
     * @param inputData {@link InputData} object.
     * @return {@link EvaluationResults} object
     */
    EvaluationResults performRequest(InputData inputData);

}
