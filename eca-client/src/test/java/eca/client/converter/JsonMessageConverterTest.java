package eca.client.converter;

import eca.client.dto.ExperimentRequestDto;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static eca.client.TestHelperUtils.createExperimentRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link JsonMessageConverter} class.
 *
 * @author Roman Batygin
 */
class JsonMessageConverterTest {

    private final JsonMessageConverter jsonMessageConverter = new JsonMessageConverter();

    @Test
    void testMessageConversion() {
        ExperimentRequestDto expected = createExperimentRequestDto();
        expected.setDataUuid(UUID.randomUUID().toString());
        byte[] bytes = jsonMessageConverter.toMessage(expected);
        assertNotNull(bytes);
        ExperimentRequestDto actual = jsonMessageConverter.fromMessage(bytes, ExperimentRequestDto.class);
        assertNotNull(actual);
        assertEquals(expected.getExperimentType(), actual.getExperimentType());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getEvaluationMethod(), actual.getEvaluationMethod());
        assertEquals(expected.getDataUuid(), actual.getDataUuid());
    }
}
