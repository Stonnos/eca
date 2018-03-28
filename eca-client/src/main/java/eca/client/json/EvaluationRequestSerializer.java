package eca.client.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eca.client.dto.EvaluationRequestDto;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.Base64;

/**
 * Evaluation request serializer.
 *
 * @author Roman Batygin
 */
public class EvaluationRequestSerializer extends JsonSerializer<EvaluationRequestDto> {

    @Override
    public void serialize(EvaluationRequestDto evaluationRequest,
                          JsonGenerator jsonGenerator,
                          SerializerProvider provider) throws IOException {

        jsonGenerator.writeStartObject();
        byte[] classifierBytes = SerializationUtils.serialize(evaluationRequest.getClassifier());
        jsonGenerator.writeStringField("classifier", Base64.getEncoder().encodeToString(classifierBytes));
        byte[] dataBytes = SerializationUtils.serialize(evaluationRequest.getData());
        jsonGenerator.writeStringField("data", Base64.getEncoder().encodeToString(dataBytes));
        jsonGenerator.writeStringField("evaluationMethod", evaluationRequest.getEvaluationMethod().name());
        jsonGenerator.writeObjectField("evaluationOptionsMap", evaluationRequest.getEvaluationOptionsMap());
        jsonGenerator.writeEndObject();
    }
}
