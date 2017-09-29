package eca.neural.functions;

/**
 * Implements soft sign activation function: <p>
 * <code>f(s) = s / (1 + |s|)</code>
 *
 * @author Roman Batygin
 */
public class SoftSignFunction extends AbstractFunction {

    /**
     * Creates <tt>SoftSignFunction</tt> object.
     */
    public SoftSignFunction() {
        super(ActivationFunctionType.SOFT_SIGN);
    }

    /**
     * Creates <tt>SoftSignFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public SoftSignFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return getCoefficient() * s / (1.0 + Math.abs(s));
    }

    @Override
    public double derivative(double s) {
        return getCoefficient() / Math.pow((1.0 + Math.abs(s)), 2);
    }

}

