package eca.client.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eca.client.dto.ExperimentRequestDto;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

import static eca.client.dictionary.JsonFieldsDictionary.DATA;
import static eca.client.dictionary.JsonFieldsDictionary.EMAIL;
import static eca.client.dictionary.JsonFieldsDictionary.EVALUATION_METHOD;
import static eca.client.dictionary.JsonFieldsDictionary.EXPERIMENT_TYPE;
import static eca.client.dictionary.JsonFieldsDictionary.FIRST_NAME;

/**
 * Experiment request serializer.
 *
 * @author Roman Batygin
 */
public class ExperimentRequestSerializer extends JsonSerializer<ExperimentRequestDto> {

    @Override
    public void serialize(ExperimentRequestDto experimentRequestDto,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(FIRST_NAME, experimentRequestDto.getFirstName());
        jsonGenerator.writeStringField(EMAIL, experimentRequestDto.getEmail());
        jsonGenerator.writeStringField(EXPERIMENT_TYPE, experimentRequestDto.getExperimentType().name());
        byte[] dataBytes = SerializationUtils.serialize(experimentRequestDto.getData());
        jsonGenerator.writeStringField(DATA, Base64.getEncoder().encodeToString(dataBytes));
        jsonGenerator.writeStringField(EVALUATION_METHOD, experimentRequestDto.getEvaluationMethod().name());
        jsonGenerator.writeEndObject();
    }
}
