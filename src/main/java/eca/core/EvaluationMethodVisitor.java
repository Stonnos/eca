package eca.core;

/**
 * @author Roman Batygin
 */
public interface EvaluationMethodVisitor<T> {

    T evaluateModel();

    T crossValidateModel();
}
