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
 *
 * @author Рома
 */
public class Neuron implements java.io.Serializable {

    public static final int IN_LAYER = 0;
    public static final int HIDDEN_LAYER = 1;
    public static final int OUT_LAYER = 2;

    private final ArrayList<NeuralLink> outLinks = new ArrayList<>();
    private final ArrayList<NeuralLink> inLinks = new ArrayList<>();
    private final int index;
    private ActivationFunction function;
    private double sumValue;
    private double outValue;
    private double error;
    private final int type;

    public Neuron(int index, ActivationFunction function, int type) {
        if (type != IN_LAYER && type != HIDDEN_LAYER && type != OUT_LAYER) {
            throw new IllegalArgumentException("Wrong values of type: " + type);
        }
        this.index = index;
        this.function = function;
        this.type = type;
    }

    public void setActivationFunction(ActivationFunction function) {
        this.function = function;
    }

    public ActivationFunction getActivationFunction() {
        return function;
    }

    public void setOutValue(double outValue) {
        this.outValue = outValue;
    }

    public int getType() {
        return type;
    }

    public int index() {
        return index;
    }

    public double getOutValue() {
        return outValue;
    }

    public void setSumValue(double sumValue) {
        this.sumValue = sumValue;
    }

    public double getSumValue() {
        return sumValue;
    }

    public void setError(double error) {
        this.error = error;
    }

    public double getError() {
        return error;
    }

    public double process(double s) {
        outValue = function.process(s);
        return outValue;
    }

    public double derivative(double s) {
        return function.derivative(s);
    }

    public double process() {
        return process(sumValue);
    }

    public double derivative() {
        return derivative(sumValue);
    }

    public void addOutLink(NeuralLink link) {
        outLinks.add(link);
    }

    public void addInLink(NeuralLink link) {
        inLinks.add(link);
    }

    public Iterator<NeuralLink> outLinks() {
        return outLinks.iterator();
    }

    public Iterator<NeuralLink> inLinks() {
        return inLinks.iterator();
    }

    public double sum() {
        sumValue = 0.0;
        for (NeuralLink link : inLinks) {
            sumValue += link.source().getOutValue() * link.getWeight();
        }
        return sumValue;
    }

}
