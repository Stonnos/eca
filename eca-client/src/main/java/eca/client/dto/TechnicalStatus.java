package eca.client.dto;

/**
 * Evaluation response status.
 *
 * @author Roman Batygin
 */
public enum TechnicalStatus {

    /**
     * Success status.
     */
    SUCCESS {
        @Override
        public void handle(TechnicalStatusVisitor visitor) {
            visitor.caseSuccessStatus();
        }
    },

    /**
     * In progress status.
     */
    IN_PROGRESS {
        @Override
        public void handle(TechnicalStatusVisitor visitor) {
            visitor.caseInProgressStatus();
        }
    },

    /**
     * Error status.
     */
    ERROR {
        @Override
        public void handle(TechnicalStatusVisitor visitor) {
            visitor.caseErrorStatus();
        }
    },

    /**
     * Timeout status.
     */
    TIMEOUT {
        @Override
        public void handle(TechnicalStatusVisitor visitor) {
            visitor.caseTimeoutStatus();
        }
    },

    /**
     * Validation error status.
     */
    VALIDATION_ERROR {
        @Override
        public void handle(TechnicalStatusVisitor visitor) {
            visitor.caseValidationErrorStatus();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     */
    public abstract void handle(TechnicalStatusVisitor visitor);

}
