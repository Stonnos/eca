package eca.client;

import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.TechnicalStatusVisitor;
import eca.client.exception.EcaServiceException;
import eca.config.EcaServiceProperties;
import eca.core.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.model.InputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

/**
 * Implements service for communication with eca - service api.
 *
 * @author Roman Batygin
 */
@Slf4j
public class RestClientImpl implements RestClient {

    private static final EcaServiceProperties PROPERTIES = EcaServiceProperties.getInstance();
    private static final String EMPTY_RESPONSE_HAS_BEEN_RECEIVED_MESSAGE = "Empty response has been received!";
    private static final String ERROR_MESSAGE_FORMAT = "These was an error [%d, %s]";
    private static final String TIMEOUT_MESSAGE = "There was a timeout.";

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

    @Override
    public EvaluationResults performRequest(InputData inputData) {

        Assert.notNull(inputData, "Input data must be specified!");
        Assert.notNull(inputData.getClassifier(), "Classifier must be specified!");
        Assert.notNull(inputData.getData(), "Instances must be specified!");

        log.info("Starting to send request into eca - service for model '{}', data '{}'.",
                inputData.getClassifier().getClass().getSimpleName(), inputData.getData().relationName());

        EvaluationRequestDto evaluationRequestDto = createRequest(inputData);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        ResponseEntity<EvaluationResponse> response = restTemplate.postForEntity(PROPERTIES.getEcaServiceUrl(),
                evaluationRequestDto, EvaluationResponse.class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            String errorMessage = String.format(ERROR_MESSAGE_FORMAT,
                    response.getStatusCode().getReasonPhrase(), response.getStatusCode().value());
            log.error(errorMessage);
            throw new EcaServiceException(errorMessage);
        }

        final EvaluationResponse classificationResultsDto = response.getBody();

        if (classificationResultsDto == null) {
            log.error(EMPTY_RESPONSE_HAS_BEEN_RECEIVED_MESSAGE);
            throw new EcaServiceException(EMPTY_RESPONSE_HAS_BEEN_RECEIVED_MESSAGE);
        }

        log.info("Received response from eca - service with status [{}] for model '{}', data '{}'.",
                classificationResultsDto.getStatus(), inputData.getClassifier().getClass().getSimpleName(),
                inputData.getData().relationName());

        return classificationResultsDto.getStatus().handle(new TechnicalStatusVisitor<EvaluationResults>() {
            @Override
            public EvaluationResults caseSuccessStatus() {
                return classificationResultsDto.getEvaluationResults();
            }

            @Override
            public EvaluationResults caseErrorStatus() {
                throw new EcaServiceException(classificationResultsDto.getErrorMessage());
            }

            @Override
            public EvaluationResults caseTimeoutStatus() {
                throw new EcaServiceException(TIMEOUT_MESSAGE);
            }
        });

    }

    private EvaluationRequestDto createRequest(InputData inputData) {
        EvaluationRequestDto evaluationRequestDto = new EvaluationRequestDto();
        evaluationRequestDto.setClassifier(inputData.getClassifier());
        evaluationRequestDto.setData(inputData.getData());
        evaluationRequestDto.setEvaluationMethod(evaluationMethod);
        if (EvaluationMethod.CROSS_VALIDATION.equals(evaluationMethod)) {
            evaluationRequestDto.setNumFolds(numFolds);
            evaluationRequestDto.setNumTests(numTests);
        }
        return evaluationRequestDto;
    }
}
