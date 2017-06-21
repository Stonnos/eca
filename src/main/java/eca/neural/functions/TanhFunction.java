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
public class TanhFunction extends AbstractFunction {

    public TanhFunction() {}

    public TanhFunction(double a) {
	super(a);
    }

    @Override
    public double process(double s) {
	return (Math.exp(coefficient()*s) - Math.exp(-coefficient()*s)) / 
                (Math.exp(coefficient()*s) + Math.exp(-coefficient()*s));
    }

    @Override
    public double derivative(double s) {
	return coefficient() *(1 - Math.pow(process(s),2));
    }

}
