/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

import java.text.DecimalFormat;

/**
 * Implements abstract activation function with getCoefficient.
 *
 * @author Roman Batygin
 */
public abstract class AbstractFunction implements ActivationFunction, java.io.Serializable, Cloneable {

    /**
     * Coefficient value
     **/
    private double coefficient = 1.0;

    protected AbstractFunction() {
    }

    protected AbstractFunction(double coefficient) {
        this.setCoefficient(coefficient);
    }

    /**
     * Returns the value of getCoefficient.
     *
     * @return the value of getCoefficient
     */
    public final double getCoefficient() {
        return coefficient;
    }

    /**
     * Sets the value of getCoefficient.
     *
     * @param coefficient the value of getCoefficient
     * @throws IllegalArgumentException if the value of getCoefficient is equal to zero
     */
    public final void setCoefficient(double coefficient) {
        if (coefficient == 0) {
            throw new IllegalArgumentException("Значение коэффициента должно быть больше нуля!");
        }
        this.coefficient = coefficient;
    }

    /**
     * Returns the string representation of getCoefficient value.
     *
     * @return the string representation of getCoefficient value
     */
    public final String coefficientToString(DecimalFormat decimalFormat) {
        return String.format("Значение коэффициента: %s", decimalFormat.format(coefficient));
    }

    @Override
    public AbstractFunction clone() throws CloneNotSupportedException {
        return (AbstractFunction) super.clone();
    }

}
