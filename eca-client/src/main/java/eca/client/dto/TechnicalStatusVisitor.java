package eca.client.dto;

/**
 * Interface for visitor pattern.
 *
 * @param <T> generic type
 * @author Roman Batygin
 */

public interface TechnicalStatusVisitor {

    /**
     * Method executed in case if technical status is SUCCESS.
     */
    void caseSuccessStatus();

    /**
     * Method executed in case if technical status is IN_PROGRESS.
     */
    void caseInProgressStatus();

    /**
     * Method executed in case if technical status is ERROR.
     */
    void caseErrorStatus();

    /**
     * Method executed in case if technical status is TIMEOUT.
     */
    void caseTimeoutStatus();

    /**
     * Method executed in case if technical status is VALIDATION_ERROR.
     */
    void caseValidationErrorStatus();

    /**
     * Method executed in case if technical status is CANCELED.
     */
    void caseCanceledStatus();
}
