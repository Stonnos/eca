package eca.core.evaluation;

/**
 * Interface for visitor pattern.
 *
 * @param <T> generic type
 * @author Roman Batygin
 */
public interface EvaluationMethodVisitor<T> {

    /**
     * Method executed in case if evaluation method is TRAINING_DATA.
     *
     * @return generic object
     */
    T evaluateModel();

    /**
     * Method executed in case if evaluation method is CROSS_VALIDATION.
     *
     * @return generic object
     */
    T crossValidateModel();
}
