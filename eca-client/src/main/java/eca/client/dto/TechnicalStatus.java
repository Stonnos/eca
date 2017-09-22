package eca.client.dto;

/**
 * Evaluation response status.
 * @author Roman Batygin
 */
public enum TechnicalStatus {

    /**
     * Success status.
     */
    SUCCESS {
        @Override
        public <T> T handle(TechnicalStatusVisitor<T> visitor) {
            return visitor.caseSuccessStatus();
        }
    },

    /**
     * Error status.
     */
    ERROR {
        @Override
        public <T> T handle(TechnicalStatusVisitor<T> visitor) {
            return visitor.caseErrorStatus();
        }
    },

    /**
     * Timeout status.
     */
    TIMEOUT {
        @Override
        public <T> T handle(TechnicalStatusVisitor<T> visitor) {
            return visitor.caseTimeoutStatus();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     * @param <T>     generic class
     * @return generic class
     */
    public abstract <T> T handle(TechnicalStatusVisitor<T> visitor);

}
