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
public class SineFunction extends AbstractFunction {
    
    public SineFunction() {}

    public SineFunction(double a) {
        super(a);
    }

    @Override
    public double process(double s) {
	return Math.sin(coefficient()*s);
    }

    @Override
    public double derivative(double s) {
	return coefficient()*Math.cos(coefficient()*s);
    }
    
}
