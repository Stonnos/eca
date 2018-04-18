package eca.data.migration.exception;

/**
 * Migration exception class.
 *
 * @author Roman Batygin
 */
public class MigrationException extends RuntimeException {

    /**
     * Creates migration exception.
     *
     * @param message - error message
     */
    public MigrationException(String message) {
        super(message);
    }
}
