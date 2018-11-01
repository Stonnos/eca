package eca.core.evaluation;

/**
 * Interface for visitor pattern.
 *
 * @author Roman Batygin
 */
public interface EvaluationMethodVisitor {

    /**
     * Method executed in case if evaluation method is TRAINING_DATA.
     */
    void evaluateModel() throws Exception;

    /**
     * Method executed in case if evaluation method is CROSS_VALIDATION.
     */
    void crossValidateModel() throws Exception;
}
