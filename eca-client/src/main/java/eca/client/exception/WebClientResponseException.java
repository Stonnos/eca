package eca.client.exception;

import lombok.Getter;

/**
 * Web client response exception.
 *
 * @author Roman Batygin
 */
@Getter
public class WebClientResponseException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    /**
     * Creates web client response exception.
     *
     * @param statusCode   - http status code
     * @param responseBody - response body
     * @param errorMessage - error message
     */
    public WebClientResponseException(int statusCode, String responseBody, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }
}
