package eca.client.http;

/**
 * Response converter interface.
 *
 * @author Roman Batygin
 */
public interface ResponseConverter {

    /**
     * Converts response body to object.
     *
     * @param responseBody - response body
     * @param clazz        - target class
     * @param <T>          - message generic type
     * @return message object
     */
    <T> T convert(String responseBody, Class<T> clazz);
}
