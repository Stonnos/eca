package eca.client.converter;

import eca.client.dto.ExperimentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static eca.client.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link JsonMessageConverter} class.
 *
 * @author Roman Batygin
 */
class JsonMessageConverterTest {

    private JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances();
    }

    @Test
    void testMessageConversion() {
        ExperimentRequestDto expected = createExperimentRequestDto();
        expected.setData(instances);
        byte[] bytes = jsonMessageConverter.toMessage(expected);
        assertNotNull(bytes);
        ExperimentRequestDto actual = jsonMessageConverter.fromMessage(bytes, ExperimentRequestDto.class);
        assertNotNull(actual);
        assertEquals(expected.getExperimentType(), actual.getExperimentType());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getEvaluationMethod(), actual.getEvaluationMethod());
        assertEquals(expected.getData().relationName(), actual.getData().relationName());
    }
}
