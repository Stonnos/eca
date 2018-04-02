/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.generators.NumberGenerator;

import java.util.Iterator;
import java.util.Objects;

/**
 * Implements multilayer perceptron learning algorithm.
 *
 * @author Roman Batygin
 */
public abstract class LearningAlgorithm implements java.io.Serializable {

    protected MultilayerPerceptron network;

    protected LearningAlgorithm(MultilayerPerceptron network) {
        Objects.requireNonNull(network, "Network is not specified!");
        this.network = network;
    }

    /**
     * Performs one iteration of learning algorithm.
     *
     * @param actual   actual outer values
     * @param expected expected outer values
     */
    public abstract void train(double[] actual, double[] expected);

    /**
     * Returns options list.
     *
     * @return options list
     */
    public abstract String[] getOptions();

    /**
     * Initialize all weight by initial values.
     */
    public void initializeWeights() {
        for (int i = 0; i < network.hiddenLayersNum(); i++) {
            for (Neuron neuron : network.hiddenLayerNeurons[i]) {
                if (i == 0) {
                    initializeLinks(neuron.inLinks());
                }
                initializeLinks(neuron.outLinks());
            }
        }
    }

    private void initializeLinks(Iterator<NeuralLink> i) {
        while (i.hasNext()) {
            NeuralLink link = i.next();
            link.setWeight(NumberGenerator.random(-0.5, 0.5));
        }
    }

}
