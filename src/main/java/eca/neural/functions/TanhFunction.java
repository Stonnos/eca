/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Implements hyperbolic tangent function: <p>
 * <code>f(s) = (exp(a*s) - exp(-a*s)) / (exp(a*s) + exp(-a*s))</code>
 *
 * @author Рома
 */
public class TanhFunction extends AbstractFunction {

    /**
     * Creates <tt>TanhFunction</tt> object.
     */
    public TanhFunction() {
    }

    /**
     * Creates <tt>TanhFunction</tt> object with given coefficient.
     *
     * @param a the value of coefficient
     */
    public TanhFunction(double a) {
        super(a);
    }

    @Override
    public double process(double s) {
        return (Math.exp(getCoefficient() * s) - Math.exp(-getCoefficient() * s)) /
                (Math.exp(getCoefficient() * s) + Math.exp(-getCoefficient() * s));
    }

    @Override
    public double derivative(double s) {
        return getCoefficient() * (1 - Math.pow(process(s), 2));
    }

}
