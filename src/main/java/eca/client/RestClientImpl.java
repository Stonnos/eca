package eca.client;

import eca.EcaServiceProperties;
import eca.model.ClassifierDescriptor;
import eca.model.InputData;
import eca.core.TestMethod;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @author Roman Batygin
 */

public class RestClientImpl implements RestClient {

    private static final EcaServiceProperties PROPERTIES = EcaServiceProperties.getInstance();

    /**
     * Evaluation test method
     **/
    private int testMethod = TestMethod.TRAINING_SET;

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
    public int getTestMethod() {
        return testMethod;
    }

    /**
     * Sets the evaluation method type
     *
     * @param testMethod the evaluation method type
     * @throws IllegalArgumentException if the specified method type
     *                                  is invalid
     */
    public void setTestMethod(int testMethod) {
        if (testMethod != TestMethod.TRAINING_SET && testMethod != TestMethod.CROSS_VALIDATION) {
            throw new IllegalArgumentException("Invalid test method value!");
        }
        this.testMethod = testMethod;
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
    public ClassifierDescriptor execute(InputData inputData) {

        Assert.notNull(inputData, "Input data must be specified!");

        if (!PROPERTIES.getEcaServiceEnabled()) {
            throw new RuntimeException("Eca service is not enabled!");
        }

        byte[] model = SerializationUtils.serialize(inputData);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<>();

        String evaluationMethod = null;

        switch (testMethod) {

            case TestMethod.TRAINING_SET: {
                evaluationMethod = PROPERTIES.getEcaServiceEvaluationMethodTraining();
                break;
            }

            case TestMethod.CROSS_VALIDATION: {
                evaluationMethod = PROPERTIES.getEcaServiceEvaluationMethodCrossValidation();
                break;
            }

        }

        if (evaluationMethod == null) {
            evaluationMethod = PROPERTIES.getEcaServiceEvaluationMethodTraining();
        }

        valueMap.add(PROPERTIES.getEcaServiceParamsModel(), new ByteArrayResource(model));
        valueMap.add(PROPERTIES.getEcaServiceParamsEvaluationMethod(), evaluationMethod);

        if (numFolds != null) {
            valueMap.add(PROPERTIES.getEcaServiceParamsNumFolds(), numFolds);
        }

        if (numTests != null) {
            valueMap.add(PROPERTIES.getEcaServiceParamsNumTests(), numTests);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(valueMap, httpHeaders);

        ResponseEntity<ByteArrayResource> response =
                restTemplate.exchange(PROPERTIES.getEcaServiceUrl(), HttpMethod.POST,
                        request, ByteArrayResource.class);

        HttpStatus httpStatus = response.getStatusCode();

        if (httpStatus.equals(HttpStatus.OK)) {
            return (ClassifierDescriptor) SerializationUtils.deserialize(response.getBody().getByteArray());
        } else {
            throw new RuntimeException(String.format("Error from server: %d %s", httpStatus.value(),
                    httpStatus.getReasonPhrase()));
        }

    }
}
