package eca.neural.functions;

/**
 * Implements inverse square root unit function: <p>
 * <code>f(s) = s / sqrt(1 + a * s^2)</code>
 *
 * @author Roman Batygin
 */
public class IsruFunction extends AbstractFunction {

    /**
     * Creates <tt>IsruFunction</tt> object.
     */
    public IsruFunction() {
        super(ActivationFunctionType.INVERSE_SQUARE_ROOT_UNIT);
    }

    /**
     * Creates <tt>IsruFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public IsruFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return s / Math.sqrt(1.0 + getCoefficient() * s * s);
    }

    @Override
    public double derivative(double s) {
        return Math.pow(1.0 / Math.sqrt(1.0 + getCoefficient() * s * s), 3);
    }
}
