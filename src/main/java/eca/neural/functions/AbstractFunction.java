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
public abstract class AbstractFunction implements ActivationFunction, java.io.Serializable {

    private double a = 1.0;

    protected AbstractFunction() {
    }

    protected AbstractFunction(double a) {
        this.setCoefficient(a);
    }

    public final double coefficient() {
        return a;
    }

    public final void setCoefficient(double a) {
        if (a == 0) {
            throw new IllegalArgumentException("Значение коэффициента должно быть больше нуля!");
        }
        this.a = a;
    }
    
    public final String coefficientToString() {
        return "Значение коэффициента: " + String.valueOf(a);
    }

}
