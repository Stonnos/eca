/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

import eca.core.Assert;
import eca.neural.NeuralNetworkDictionary;

/**
 * Implements abstract activation function with getCoefficient.
 *
 * @author Roman Batygin
 */
public abstract class AbstractFunction implements ActivationFunction {

    /**
     * Activation function type
     */
    private ActivationFunctionType activationFunctionType;

    /**
     * Coefficient value
     **/
    private double coefficient = ActivationFunctionsDictionary.DEFAULT_COEFFICIENT;

    protected AbstractFunction(ActivationFunctionType activationFunctionType) {
        this.activationFunctionType = activationFunctionType;
    }

    protected AbstractFunction(double coefficient) {
        this.setCoefficient(coefficient);
    }

    /**
     * Returns the value of getCoefficient.
     *
     * @return the value of getCoefficient
     */
    public double getCoefficient() {
        return coefficient;
    }

    /**
     * Sets the value of getCoefficient.
     *
     * @param coefficient the value of getCoefficient
     * @throws IllegalArgumentException if the value of getCoefficient is equal to zero
     */
    public void setCoefficient(double coefficient) {
        Assert.greaterThanZero(coefficient, NeuralNetworkDictionary.BAD_AF_COEFFICIENT_VALUE_ERROR_TEXT);
        this.coefficient = coefficient;
    }

    @Override
    public ActivationFunctionType getActivationFunctionType() {
        return activationFunctionType;
    }

}
