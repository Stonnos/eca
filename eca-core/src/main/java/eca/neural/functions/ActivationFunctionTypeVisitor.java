package eca.neural.functions;

/**
 * Activation function type visitor interface.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface ActivationFunctionTypeVisitor<T> {

    T caseLogistic();

    T caseHyperbolicTangent();

    T caseSine();

    T caseExponential();

    T caseSoftSign();

}
