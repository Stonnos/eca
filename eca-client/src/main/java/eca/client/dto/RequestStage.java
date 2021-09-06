package eca.client.dto;

/**
 * Request stage enum.
 *
 * @author Roman Batygin
 */
public enum RequestStage {

    /**
     * Request created
     */
    CREATED {
        @Override
        public void handle(RequestStageVisitor visitor) {
            visitor.caseCreated();
        }
    },

    /**
     * Request finished
     */
    FINISHED {
        @Override
        public void handle(RequestStageVisitor visitor) {
            visitor.caseFinished();
        }
    };

    /**
     * Visitor pattern common method
     *
     * @param visitor visitor class
     */
    public abstract void handle(RequestStageVisitor visitor);
}
