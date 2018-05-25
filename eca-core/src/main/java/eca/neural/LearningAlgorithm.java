/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.generators.NumberGenerator;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

/**
 * Implements multilayer perceptron learning algorithm.
 *
 * @author Roman Batygin
 */
public abstract class LearningAlgorithm implements java.io.Serializable {

    private static final double LOWER_BOUND = -0.5;
    private static final double UPPER_BOUND = 0.5;

    private Random random = new Random();

    protected MultilayerPerceptron network;

    protected LearningAlgorithm(MultilayerPerceptron network) {
        Objects.requireNonNull(network, "Network is not specified!");
        this.network = network;
    }

    /**
     * Returns random object.
     *
     * @return random object
     */
    public Random getRandom() {
        return random;
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
            link.setWeight(NumberGenerator.random(random, LOWER_BOUND, UPPER_BOUND));
        }
    }

}
