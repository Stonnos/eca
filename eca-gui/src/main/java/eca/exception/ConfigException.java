package eca.exception;

/**
 * Config exception
 *
 * @author Roman Batygin
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates config exception.
     *
     * @param throwable - throwable interface
     */
    public ConfigException(Throwable throwable) {
        super(throwable);
    }
}
