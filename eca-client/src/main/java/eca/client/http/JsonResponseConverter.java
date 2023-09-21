package eca.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Json response converter.
 *
 * @author Roman Batygin
 */
public class JsonResponseConverter implements ResponseConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T convert(String responseBody, Class<T> clazz) {
        try {
            return objectMapper.readValue(responseBody, clazz);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }
}
