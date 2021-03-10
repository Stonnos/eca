package eca.client.dto;

/**
 * Interface for visitor pattern.
 *
 * @param <T> generic type
 * @author Roman Batygin
 */

public interface TechnicalStatusVisitor<T> {

    /**
     * Method executed in case if technical status is SUCCESS.
     *
     * @return generic object
     */
    T caseSuccessStatus();

    /**
     * Method executed in case if technical status is ERROR.
     *
     * @return generic object
     */
    T caseErrorStatus();

    /**
     * Method executed in case if technical status is TIMEOUT.
     *
     * @return generic object
     */
    T caseTimeoutStatus();

    /**
     * Method executed in case if technical status is VALIDATION_ERROR.
     *
     * @return generic object
     */
    T caseValidationErrorStatus();
}
