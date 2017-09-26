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
 * @author Roman Batygin
 */
public class HyperbolicTangentFunction extends AbstractFunction {

    /**
     * Creates <tt>HyperbolicTangentFunction</tt> object.
     */
    public HyperbolicTangentFunction() {
        super(ActivationFunctionType.HYPERBOLIC_TANGENT);
    }

    /**
     * Creates <tt>HyperbolicTangentFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public HyperbolicTangentFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return (Math.exp(getCoefficient() * s) - Math.exp(- getCoefficient() * s)) /
                (Math.exp(getCoefficient() * s) + Math.exp(- getCoefficient() * s));
    }

    @Override
    public double derivative(double s) {
        return getCoefficient() * (1 - Math.pow(process(s), 2));
    }

}
