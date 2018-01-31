/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.text.NumericFormatFactory;

import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * Implements back propagation algorithm. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Sets the value of learning rate (Default: 0.1) <p>
 * <p>
 * Sets the value of momentum (Default: 0.2) <p>
 *
 * @author Roman Batygin
 */
public class BackPropagation extends LearningAlgorithm {

    public static final double MIN_LEARNING_RATE = 0.0;
    public static final double MAX_LEARNING_RATE = 1.0;
    public static final double MIN_MOMENTUM = 0.0;
    public static final double MAX_MOMENTUM = 1.0;

    private static final DecimalFormat COMMON_DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);

    /**
     * Learning rate value
     **/
    private double learningRate = 0.1;

    /**
     * Momentum value
     **/
    private double momentum = 0.2;

    /**
     * Creates <tt>BackPropagation</tt> object.
     *
     * @param network <tt>MultilayerPerceptron</tt> object.
     */
    public BackPropagation(MultilayerPerceptron network) {
        super(network);
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        if (learningRate <= MIN_LEARNING_RATE || learningRate > MAX_LEARNING_RATE) {
            throw new IllegalArgumentException(
                    String.format(NeuralNetworkDictionary.BAD_LEARNING_SPEED_ERROR_FORMAT,
                            MIN_LEARNING_RATE, MAX_LEARNING_RATE));
        }
        this.learningRate = learningRate;
    }

    public double getMomentum() {
        return momentum;
    }

    @Override
    public String[] getOptions() {
        return new String[]{NeuralNetworkDictionary.LEARNING_SPEED, COMMON_DECIMAL_FORMAT.format(learningRate),
                NeuralNetworkDictionary.MOMENTUM, COMMON_DECIMAL_FORMAT.format(momentum)};
    }

    public void setMomentum(double momentum) {
        if (momentum < MIN_MOMENTUM || momentum >= MAX_MOMENTUM) {
            throw new IllegalArgumentException(
                    String.format(NeuralNetworkDictionary.BAD_MOMENTUM_ERROR_FORMAT,
                            MIN_LEARNING_RATE, MAX_LEARNING_RATE));
        }
        this.momentum = momentum;
    }

    @Override
    public void train(double[] actual, double[] expected) {
        for (int i = 0; i < network.outLayerNeuronsNum(); i++) {
            Neuron u = network.outLayerNeurons[i];
            u.setError((expected[i] - actual[i]) * u.derivative());
            correctWeight(u);
        }

        for (int i = network.hiddenLayersNum() - 1; i >= 0; i--) {
            for (Neuron u : network.hiddenLayerNeurons[i]) {
                u.setError(sumError(u) * u.derivative());
                correctWeight(u);
            }
        }
    }

    private void correctWeight(Neuron u) {
        for (Iterator<NeuralLink> e = u.inLinks(); e.hasNext(); ) {
            NeuralLink link = e.next();
            Neuron v = link.source();
            double dw = learningRate * u.getError() * v.getOutValue();
            dw += link.getPreviousCorrect() * momentum;
            link.setPreviousCorrect(dw);
            link.setWeight(link.getWeight() + dw);
        }
    }

    private double sumError(Neuron u) {
        double s = 0.0;
        for (Iterator<NeuralLink> i = u.outLinks(); i.hasNext(); ) {
            NeuralLink e = i.next();
            s += e.target().getError() * e.getWeight();
        }
        return s;
    }

}
