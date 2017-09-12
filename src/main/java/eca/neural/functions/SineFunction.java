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
public class SineFunction extends AbstractFunction {

    /**
     * Creates <tt>SineFunction</tt> object.
     */
    public SineFunction() {
    }

    /**
     * Creates <tt>SineFunction</tt> object with given coefficient.
     *
     * @param a the value of coefficient
     */
    public SineFunction(double a) {
        super(a);
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
