package eca.client.exception;

/**
 * Eca - service exception.
 *
 * @author Roman Batygin
 */
public class EcaServiceException extends RuntimeException {

    /**
     * Constructor with message.
     *
     * @param message - error message
     */
    public EcaServiceException(String message) {
        super(message);
    }
}
