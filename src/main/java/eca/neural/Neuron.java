/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.neural.functions.ActivationFunction;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Neuron model.
 *
 * @author Рома
 */
public class Neuron implements java.io.Serializable {

    public static final int IN_LAYER = 0;
    public static final int HIDDEN_LAYER = 1;
    public static final int OUT_LAYER = 2;

    /**
     * Outer links list
     **/
    private final ArrayList<NeuralLink> outLinks = new ArrayList<>();

    /**
     * Inner links list
     **/
    private final ArrayList<NeuralLink> inLinks = new ArrayList<>();

    /**
     * Neuron index
     **/
    private final int index;

    /**
     * Activation function
     **/
    private ActivationFunction function;

    /**
     * Input summary value
     **/
    private double sumValue;

    /**
     * Output value
     **/
    private double outValue;

    /**
     * Neuron error (using by back propagation algorithm)
     **/
    private double error;

    /**
     * Neuron type
     **/
    private final int type;

    /**
     * Creates <tt>Neuron</tt> object with given options.
     *
     * @param index    neuron index
     * @param function activation function
     * @param type     neuron type
     */
    public Neuron(int index, ActivationFunction function, int type) {
        if (type != IN_LAYER && type != HIDDEN_LAYER && type != OUT_LAYER) {
            throw new IllegalArgumentException("Wrong values of type: " + type);
        }
        this.index = index;
        this.function = function;
        this.type = type;
    }

    /**
     * Sets activation function.
     *
     * @param function activation function
     */
    public void setActivationFunction(ActivationFunction function) {
        this.function = function;
    }

    /**
     * Returns activation function.
     *
     * @return activation function
     */
    public ActivationFunction getActivationFunction() {
        return function;
    }

    /**
     * Sets the value of neuron output value.
     *
     * @param outValue the value of neuron outer value
     */
    public void setOutValue(double outValue) {
        this.outValue = outValue;
    }

    /**
     * Returns neuron type.
     *
     * @return neuron type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the value of neuron index.
     *
     * @return the value of neuron index
     */
    public int index() {
        return index;
    }

    /**
     * Returns the value of neuron output value.
     *
     * @return the value of neuron output value
     */
    public double getOutValue() {
        return outValue;
    }

    /**
     * Sets the value of neuron input summary.
     *
     * @param sumValue the value of neuron input summary
     */
    public void setSumValue(double sumValue) {
        this.sumValue = sumValue;
    }

    /**
     * Returns the value of neuron input summary.
     *
     * @return the value of neuron input summary
     */
    public double getSumValue() {
        return sumValue;
    }

    /**
     * Sets the value of neuron error (using by back propagation algorithm).
     *
     * @param error the value of neuron error
     */
    public void setError(double error) {
        this.error = error;
    }

    /**
     * Returns the value of neuron error (using by back propagation algorithm).
     *
     * @return the value of neuron error
     */
    public double getError() {
        return error;
    }

    /**
     * Calculates the neuron output value from given input summary.
     *
     * @param s the value of input summary
     * @return the neuron output value
     */
    public double process(double s) {
        outValue = function.process(s);
        return outValue;
    }

    /**
     * Calculates the value of activation function derivative
     * from given input summary.
     *
     * @param s the value of input summary
     * @return the value of activation function derivative
     */
    public double derivative(double s) {
        return function.derivative(s);
    }

    /**
     * Calculates the neuron output value.
     *
     * @return the neuron output value
     */
    public double process() {
        return process(sumValue);
    }

    /**
     * Calculates the value of activation function derivative.
     *
     * @return the value of activation function derivative
     */
    public double derivative() {
        return derivative(sumValue);
    }

    /**
     * Adds output link.
     *
     * @param link neural link
     */
    public void addOutLink(NeuralLink link) {
        outLinks.add(link);
    }

    /**
     * Adds input link.
     *
     * @param link input link
     */
    public void addInLink(NeuralLink link) {
        inLinks.add(link);
    }

    /**
     * Returns an <tt>Iterator</tt> object over the output links.
     *
     * @return an <tt>Iterator</tt> object over the output links
     */
    public Iterator<NeuralLink> outLinks() {
        return outLinks.iterator();
    }

    /**
     * Returns an <tt>Iterator</tt> object over the input links.
     *
     * @return an <tt>Iterator</tt> object over the input links
     */
    public Iterator<NeuralLink> inLinks() {
        return inLinks.iterator();
    }

    /**
     * Calculates the summary value of input signals.
     *
     * @return the summary value of input signals
     */
    public double sum() {
        sumValue = 0.0;
        for (NeuralLink link : inLinks) {
            sumValue += link.source().getOutValue() * link.getWeight();
        }
        return sumValue;
    }

}
