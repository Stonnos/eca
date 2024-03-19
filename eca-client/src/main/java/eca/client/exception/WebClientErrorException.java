package eca.client.exception;

/**
 * Web client error exception.
 *
 * @author Roman Batygin
 */
public class WebClientErrorException extends RuntimeException {

    /**
     * Creates web client error exception.
     *
     * @param errorMessage - error message
     */
    public WebClientErrorException(String errorMessage) {
        super(errorMessage);
    }
}
