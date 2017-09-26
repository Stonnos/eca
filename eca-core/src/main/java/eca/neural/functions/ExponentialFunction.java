/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Implements exponential activation function: <p>
 * <code>f(s) = exp(-s^2/a^2)</code>
 *
 * @author Roman Batygin
 */
public class ExponentialFunction extends AbstractFunction {

    /**
     * Creates <tt>ExponentialFunction</tt> object.
     */
    public ExponentialFunction() {
        super(ActivationFunctionType.EXPONENTIAL);
    }

    /**
     * Creates <tt>ExponentialFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public ExponentialFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return Math.exp(-(s * s) / (getCoefficient() * getCoefficient()));
    }

    @Override
    public double derivative(double s) {
        return (-2.0 * s * process(s)) / (getCoefficient() * getCoefficient());
    }

}
