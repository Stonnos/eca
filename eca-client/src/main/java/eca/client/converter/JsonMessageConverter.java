package eca.client.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Json message converter.
 *
 * @author Roman Batygin
 */
public class JsonMessageConverter implements MessageConverter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> byte[] toMessage(T object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    @Override
    public <T> T fromMessage(byte[] message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, 0, message.length, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
