/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

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
 * @author Рома
 */
public class BackPropagation extends LearningAlgorithm {

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
        if (learningRate <= 0.0 || learningRate > 1.0) {
            throw new IllegalArgumentException(
                    "Недопустимое значение скорости обучения: " + String.valueOf(learningRate));
        }
        this.learningRate = learningRate;
    }

    public double getMomentum() {
        return momentum;
    }

    @Override
    public String[] getOptions() {
        String[] options = {"Коэффициент скорости обучения:", String.valueOf(learningRate),
                "Коэффициент момента:", String.valueOf(momentum)};
        return options;
    }

    public void setMomentum(double momentum) {
        if (momentum < 0.0 || momentum >= 1.0) {
            throw new IllegalArgumentException("Недопустимое значение момента: " + String.valueOf(momentum));
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
