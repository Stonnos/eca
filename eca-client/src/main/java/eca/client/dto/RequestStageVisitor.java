package eca.client.dto;

/**
 * Request stage visitor.
 *
 * @author Roman Batygin
 */
public interface RequestStageVisitor {

    /**
     * Case request created.
     */
    void caseCreated();

    /**
     * Case request finished.
     */
    void caseFinished();
}
