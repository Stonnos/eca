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
public class LogisticFunction extends AbstractFunction {
    

    public LogisticFunction() {}

    public LogisticFunction(double a) {
        super(a);
    }

    @Override
    public double process(double s) {
	return 1.0 / (1.0 + Math.exp(-coefficient()*s));
    }

    @Override
    public double derivative(double s) {
	return coefficient() * process(s) * (1 - process(s));
    }

}
