package eca;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.primitives.Bytes;
import eca.gui.frames.JMainFrame;
import eca.trees.ID3;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.StdSerializers;
import org.springframework.util.SerializationUtils;
import weka.classifiers.Classifier;

import java.awt.*;
import java.io.IOException;
import java.util.Base64;

/**
 * Main class.
 *
 * @author Roman Batygin
 */
@Slf4j
public class Eca {

    @JsonSerialize(using = BytesSerializer.class)
    @JsonDeserialize(using = BytesDeserializer.class)
    static class Model {
        Classifier classifier;
    }

    static class BytesSerializer extends JsonSerializer<Model> {


        @Override
        public void serialize(Model model, org.codehaus.jackson.JsonGenerator jsonGenerator,
                              SerializerProvider provider)
                throws IOException, JsonProcessingException {

            jsonGenerator.writeStartObject();
            byte[] bytes = SerializationUtils.serialize(model.classifier);
            jsonGenerator.writeStringField("classifier", Base64.getEncoder().encodeToString(bytes));
            jsonGenerator.writeEndObject();
        }
    }

    static class BytesDeserializer extends JsonDeserializer<Model> {


        @Override
        public Model deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            JsonNode jsonNode = jp.getCodec().readTree(jp);
            Model model = new Model();
            String string = jsonNode.get("classifier").getTextValue();
            byte[] bytes = Base64.getDecoder().decode(string);
            model.classifier = (Classifier) SerializationUtils.deserialize(bytes);

            return model;
        }
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JMainFrame().setVisible(true);
                log.info("Eca application was started.");
            }
        });


        Model model = new Model();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            model.classifier = new ID3();

            String json = objectMapper.writeValueAsString(model);

            System.out.println(json);

            Model target = objectMapper.readValue(json, Model.class);

            System.out.println(target.classifier.getClass().getSimpleName());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
