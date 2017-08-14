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
 * @author Рома
 */
public class ExponentialFunction extends AbstractFunction {

    /**
     * Creates <tt>ExponentialFunction</tt> object.
     */
    public ExponentialFunction() {
    }

    /**
     * Creates <tt>ExponentialFunction</tt> object with given coefficient.
     *
     * @param a the value of coefficient
     */
    public ExponentialFunction(double a) {
        super(a);
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
