/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Implements logistic activation function: <p>
 * <code>f(s) = 1 / (1 + exp(-a*s))</code>
 *
 * @author Roman Batygin
 */
public class LogisticFunction extends AbstractFunction {

    /**
     * Creates <tt>LogisticFunction</tt> object.
     */
    public LogisticFunction() {
        super(ActivationFunctionType.LOGISTIC);
    }

    /**
     * Creates <tt>LogisticFunction</tt> object with given coefficient.
     *
     * @param coefficient the value of coefficient
     */
    public LogisticFunction(double coefficient) {
        super(coefficient);
    }

    @Override
    public double process(double s) {
        return 1.0 / (1.0 + Math.exp(-getCoefficient() * s));
    }

    @Override
    public double derivative(double s) {
        return getCoefficient() * process(s) * (1 - process(s));
    }

}
