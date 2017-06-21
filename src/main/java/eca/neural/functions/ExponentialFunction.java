/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 *
 * @author Рома
 */
public class ExponentialFunction extends AbstractFunction {
    
    public ExponentialFunction() {}

    public ExponentialFunction(double a) {
        super(a);
    }

    @Override
    public double process(double s) {
	return Math.exp(-(s*s)/(coefficient()*coefficient()));
    }

    @Override
    public double derivative(double s) {
	return (-2.0*s*process(s))/(coefficient()*coefficient());
    }
    
}
