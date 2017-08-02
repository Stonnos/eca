package eca.client;

import eca.EcaServiceProperties;
import eca.beans.ClassifierDescriptor;
import eca.beans.InputData;
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

    private int testMethod = TestMethod.TRAINING_SET;

    private Integer numFolds;

    private Integer numTests;

    public int getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(int testMethod) {
        if (testMethod != TestMethod.TRAINING_SET && testMethod != TestMethod.CROSS_VALIDATION) {
            throw new IllegalArgumentException("Invalid test method value!");
        }
        this.testMethod = testMethod;
    }

    public Integer getNumFolds() {
        return numFolds;
    }

    public void setNumFolds(Integer numFolds) {
        this.numFolds = numFolds;
    }

    public Integer getNumTests() {
        return numTests;
    }

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
