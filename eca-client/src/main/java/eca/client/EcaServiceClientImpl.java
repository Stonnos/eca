package eca.client;

import eca.client.dto.EcaResponse;
import eca.client.dto.EvaluationOption;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.TechnicalStatusVisitor;
import eca.client.exception.EcaServiceException;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.HashMap;

import static eca.client.util.Utils.validateResponse;

/**
 * Implements service for communication with eca - service API.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EcaServiceClientImpl implements EcaServiceClient {

    private static final String TIMEOUT_MESSAGE = "There was a timeout.";

    /**
     * Eca - service evaluation url.
     */
    private String evaluationUrl;

    /**
     * Eca - service experiment url.
     */
    private String experimentUrl;

    /**
     * Evaluation test method
     **/
    private EvaluationMethod evaluationMethod = EvaluationMethod.TRAINING_DATA;

    /**
     * Number of folds
     **/
    private Integer numFolds;

    /**
     * Number of tests
     **/
    private Integer numTests;

    /**
     * Rest template object
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Return the evaluation method type.
     *
     * @return the evaluation method type
     */
    public EvaluationMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    /**
     * Sets the evaluation method type
     *
     * @param evaluationMethod the evaluation method type
     * @throws IllegalArgumentException if the specified method type
     *                                  is invalid
     */
    public void setEvaluationMethod(EvaluationMethod evaluationMethod) {
        Assert.notNull(evaluationMethod, "Evaluation method is not specified!");
        this.evaluationMethod = evaluationMethod;
    }

    /**
     * Returns the number of folds.
     *
     * @return the number of folds
     */
    public Integer getNumFolds() {
        return numFolds;
    }

    /**
     * Sets the number of folds.
     *
     * @param numFolds the number of folds
     */
    public void setNumFolds(Integer numFolds) {
        this.numFolds = numFolds;
    }

    /**
     * Returns the number of validations.
     *
     * @return the number of validations
     */
    public Integer getNumTests() {
        return numTests;
    }

    /**
     * Sets the number of validations.
     *
     * @param numTests the number of validations
     */
    public void setNumTests(Integer numTests) {
        this.numTests = numTests;
    }

    /**
     * Returns eca - service classifier evaluation API URL.
     *
     * @return eca - service classifier evaluation API URL
     */
    public String getEvaluationUrl() {
        return evaluationUrl;
    }

    /**
     * Sets eca - service classifier evaluation API URL.
     *
     * @param evaluationUrl - evaluation URL
     */
    public void setEvaluationUrl(String evaluationUrl) {
        this.evaluationUrl = evaluationUrl;
    }

    /**
     * Returns eca - service classifier experiment API URL.
     *
     * @return eca - service classifier experiment API URL
     */
    public String getExperimentUrl() {
        return experimentUrl;
    }

    /**
     * Sets eca - service classifier experiment API URL
     *
     * @param experimentUrl - experiment URL
     */
    public void setExperimentUrl(String experimentUrl) {
        this.experimentUrl = experimentUrl;
    }

    @Override
    public EvaluationResults performRequest(AbstractClassifier classifier, Instances data) {
        Assert.notNull(classifier, "Classifier must be specified!");
        Assert.notNull(data, "Instances must be specified!");

        log.info("Starting to send request into eca - service for model '{}', data '{}'.",
                classifier.getClass().getSimpleName(), data.relationName());
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequest(classifier, data);
        ResponseEntity<EvaluationResponse> response = restTemplate.postForEntity(evaluationUrl,
                evaluationRequestDto, EvaluationResponse.class);
        validateResponse(response);
        final EvaluationResponse evaluationResponse = response.getBody();

        log.info("Received response from eca - service with status [{}] for model '{}', data '{}'.",
                evaluationResponse.getStatus(), classifier.getClass().getSimpleName(), data.relationName());

        return evaluationResponse.getStatus().handle(new TechnicalStatusVisitor<EvaluationResults>() {
            @Override
            public EvaluationResults caseSuccessStatus() {
                return evaluationResponse.getEvaluationResults();
            }

            @Override
            public EvaluationResults caseErrorStatus() {
                throw new EcaServiceException(evaluationResponse.getErrorMessage());
            }

            @Override
            public EvaluationResults caseTimeoutStatus() {
                throw new EcaServiceException(TIMEOUT_MESSAGE);
            }
        });

    }

    @Override
    public EcaResponse createExperimentRequest(ExperimentRequestDto experimentRequestDto) {
        Assert.notNull(experimentRequestDto, "Experiment request is not specified!");
        log.info("Starting to send request into eca - service for experiment '{}', data '{}'.",
                experimentRequestDto.getExperimentType(), experimentRequestDto.getData().relationName());
        ResponseEntity<EcaResponse> response =
                restTemplate.postForEntity(experimentUrl, experimentRequestDto, EcaResponse.class);
        validateResponse(response);
        EcaResponse ecaResponse = response.getBody();
        log.info("Received response from eca - service with status [{}] for experiment request {}.",
                ecaResponse.getStatus(), experimentRequestDto.getExperimentType());

        return ecaResponse;
    }

    private EvaluationRequestDto createEvaluationRequest(AbstractClassifier classifier, Instances data) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(classifier);
        evaluationRequestDto.setData(data);
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDto.setEvaluationOptionsMap(new HashMap<>());
            if (numFolds != null) {
                evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_FOLDS,
                        String.valueOf(numFolds));
            }
            if (numTests != null) {
                evaluationRequestDto.getEvaluationOptionsMap().put(EvaluationOption.NUM_TESTS,
                        String.valueOf(numTests));
            }
        }
        return evaluationRequestDto;
    }
}
