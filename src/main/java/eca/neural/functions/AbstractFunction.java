/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Implements abstract activation function with getCoefficient.
 *
 * @author Рома
 */
public abstract class AbstractFunction implements ActivationFunction, java.io.Serializable {

    /**
     * Coefficient value
     **/
    private double a = 1.0;

    protected AbstractFunction() {
    }

    protected AbstractFunction(double a) {
        this.setCoefficient(a);
    }

    /**
     * Returns the value of getCoefficient.
     *
     * @return the value of getCoefficient
     */
    public final double getCoefficient() {
        return a;
    }

    /**
     * Sets the value of getCoefficient.
     *
     * @param a the value of getCoefficient
     * @throws IllegalArgumentException if the value of getCoefficient is equal to zero
     */
    public final void setCoefficient(double a) {
        if (a == 0) {
            throw new IllegalArgumentException("Значение коэффициента должно быть больше нуля!");
        }
        this.a = a;
    }

    /**
     * Returns the string representation of getCoefficient value.
     *
     * @return the string representation of getCoefficient value
     */
    public final String coefficientToString() {
        return "Значение коэффициента: " + String.valueOf(a);
    }

}
