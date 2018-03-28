package eca.client.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eca.client.dto.ExperimentRequestDto;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

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
        jsonGenerator.writeStringField("firstName", experimentRequestDto.getFirstName());
        jsonGenerator.writeStringField("email", experimentRequestDto.getEmail());
        jsonGenerator.writeStringField("experimentType", experimentRequestDto.getExperimentType().name());
        byte[] dataBytes = SerializationUtils.serialize(experimentRequestDto.getData());
        jsonGenerator.writeStringField("data", Base64.getEncoder().encodeToString(dataBytes));
        jsonGenerator.writeStringField("evaluationMethod", experimentRequestDto.getEvaluationMethod().name());
        jsonGenerator.writeEndObject();
    }
}
