/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Implements sine activation function: <p>
 * <code>f(s) = sin(a*s)</code>
 *
 * @author Roman Batygin
 */
public class SinusoidFunction extends AbstractFunction {

    /**
     * Creates <tt>SinusoidFunction</tt> object.
     */
    public SinusoidFunction() {
        super(ActivationFunctionType.SINUSOID);
    }

    /**
     * Creates <tt>SinusoidFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public SinusoidFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return Math.sin(getCoefficient() * s);
    }

    @Override
    public double derivative(double s) {
        return getCoefficient() * Math.cos(getCoefficient() * s);
    }

}
