package eca.neural.functions;

/**
 * Activation function type visitor interface.
 *
 * @param <T> - generic type
 * @author Roman Batygin
 */
public interface ActivationFunctionTypeVisitor<T> {

    /**
     * Method executed in case if activation function type is LOGISTIC.
     *
     * @return generic object
     */
    T caseLogistic();

    /**
     * Method executed in case if activation function type is HYPERBOLIC_TANGENT.
     *
     * @return generic object
     */
    T caseHyperbolicTangent();

    /**
     * Method executed in case if activation function type is SINUSOID.
     *
     * @return generic object
     */
    T caseSinusoid();

    /**
     * Method executed in case if activation function type is EXPONENTIAL.
     *
     * @return generic object
     */
    T caseExponential();

    /**
     * Method executed in case if activation function type is SOFT_SIGN.
     *
     * @return generic object
     */
    T caseSoftSign();

    /**
     * Method executed in case if activation function type is INVERSE_SQUARE_ROOT_UNIT.
     *
     * @return generic object
     */
    T caseInverseSquareRootUnit();

}
