package eca.client;

import eca.client.dto.EcaResponse;
import eca.client.dto.EvaluationOption;
import eca.client.dto.EvaluationRequestDto;
import eca.client.dto.EvaluationResponse;
import eca.client.dto.ExperimentRequestDto;
import eca.client.dto.InstancesRequest;
import eca.client.dto.TechnicalStatusVisitor;
import eca.client.exception.EcaServiceException;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.EnumMap;

import static eca.client.util.Utils.validateResponse;

/**
 * Implements service for communication with eca - service API.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EcaServiceClientImpl implements EcaServiceClient {

    private static final String TIMEOUT_MESSAGE = "There was a timeout.";
    private static final String EVALUATION_URL_FORMAT = "%s/evaluation/execute";
    private static final String OPTIMIZER_URL_FORMAT = "%s/evaluation/optimize";
    private static final String EXPERIMENT_URL_FORMAT = "%s/experiment/create";
    private static final String TOKEN_URL_FORMAT = "%s/oauth/token";

    /**
     * Eca - service details
     */
    @Getter
    private EcaServiceDetails ecaServiceDetails = EcaServiceDetails.builder().build();

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
     * Rest template object
     */
    private OAuth2RestTemplate restTemplate;

    /**
     * Default constructor.
     */
    public EcaServiceClientImpl() {
        updateOauth2RestTemplateResourceDetails();
    }

    /**
     * Sets eca - service details.
     *
     * @param ecaServiceDetails - eca - service details
     */
    public void setEcaServiceDetails(EcaServiceDetails ecaServiceDetails) {
        Assert.notNull(ecaServiceDetails, "Eca - service details must be specified!");
        this.ecaServiceDetails = ecaServiceDetails;
        updateOauth2RestTemplateResourceDetails();
    }

    @Override
    public EvaluationResults performRequest(AbstractClassifier classifier, Instances data) {
        Assert.notNull(classifier, "Classifier must be specified!");
        Assert.notNull(data, "Instances must be specified!");
        log.info("Starting to send request into eca - service for model '{}', data '{}'.",
                classifier.getClass().getSimpleName(), data.relationName());
        EvaluationRequestDto evaluationRequestDto = createEvaluationRequest(classifier, data);
        String evaluationUrl = String.format(EVALUATION_URL_FORMAT, ecaServiceDetails.getApiUrl());
        ResponseEntity<EvaluationResponse> response = restTemplate.postForEntity(evaluationUrl,
                evaluationRequestDto, EvaluationResponse.class);
        validateResponse(response);
        EvaluationResponse evaluationResponse = response.getBody();
        log.info("Received response from eca - service with id [{}], status [{}] for model '{}', data '{}'.",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus(),
                classifier.getClass().getSimpleName(), data.relationName());
        return handleEvaluationResponse(evaluationResponse);
    }

    @Override
    public EcaResponse createExperimentRequest(ExperimentRequestDto experimentRequestDto) {
        Assert.notNull(experimentRequestDto, "Experiment request is not specified!");
        log.info("Starting to send request into eca - service for experiment '{}', data '{}'.",
                experimentRequestDto.getExperimentType(), experimentRequestDto.getData().relationName());
        String experimentUrl = String.format(EXPERIMENT_URL_FORMAT, ecaServiceDetails.getApiUrl());
        ResponseEntity<EcaResponse> response =
                restTemplate.postForEntity(experimentUrl, experimentRequestDto, EcaResponse.class);
        validateResponse(response);
        EcaResponse ecaResponse = response.getBody();
        log.info("Received response from eca - service with id [{}], status [{}] for experiment request {}.",
                ecaResponse.getRequestId(), ecaResponse.getStatus(), experimentRequestDto.getExperimentType());
        return ecaResponse;
    }

    @Override
    public EvaluationResults performRequest(Instances data) {
        Assert.notNull(data, "Instances must be specified!");
        log.info("Starting to send request into eca - service for data '{}'.", data.relationName());
        String optimalClassifierUrl = String.format(OPTIMIZER_URL_FORMAT, ecaServiceDetails.getApiUrl());
        ResponseEntity<EvaluationResponse> response =
                restTemplate.postForEntity(optimalClassifierUrl, new InstancesRequest(data), EvaluationResponse.class);
        validateResponse(response);
        EvaluationResponse evaluationResponse = response.getBody();
        log.info("Received response from eca - service with id [{}], status [{}] for data '{}'.",
                evaluationResponse.getRequestId(), evaluationResponse.getStatus(), data.relationName());
        return handleEvaluationResponse(evaluationResponse);
    }

    private ClientCredentialsResourceDetails clientCredentialsResourceDetails(){
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setClientId(ecaServiceDetails.getClientId());
        resourceDetails.setClientSecret(ecaServiceDetails.getClientSecret());
        resourceDetails.setAccessTokenUri(String.format(TOKEN_URL_FORMAT, ecaServiceDetails.getTokenUrl()));
        return resourceDetails;
    }

    private void updateOauth2RestTemplateResourceDetails() {
        ClientCredentialsResourceDetails resourceDetails = clientCredentialsResourceDetails();
        restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ClientCredentialsAccessTokenProvider());
    }

    private EvaluationResults handleEvaluationResponse(final EvaluationResponse evaluationResponse) {
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
