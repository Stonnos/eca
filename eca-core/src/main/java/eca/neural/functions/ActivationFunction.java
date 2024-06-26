/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural.functions;

/**
 * Interface for neuron activation function.
 *
 * @author Roman Batygin
 */
public interface ActivationFunction extends java.io.Serializable {

    /**
     * Calculates the value of activation function.
     *
     * @param s argument value
     * @return the value of activation function
     */
    double process(double s);

    /**
     * Calculates the value of activation function derivative.
     *
     * @param s the argument value
     * @return the value of activation function derivative
     */
    double derivative(double s);

    /**
     * Returns activation function type.
     * @return {@link ActivationFunctionType} object
     */
    ActivationFunctionType getActivationFunctionType();
}
