/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.generators.NumberGenerator;
import eca.model.ClassifierDescriptor;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.AbstractFunction;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options
 * for neural networks based on experiment series.
 *
 * @author Roman93
 */
public class AutomatedNeuralNetwork extends AbstractExperiment<NeuralNetwork> {

    private static final double MIN_COEFFICIENT_VALUE = 1.0;
    private static final double MAX_COEFFICIENT_VALUE = 5.0;

    /**
     * Available activation functions
     **/
    private List<AbstractFunction> activationFunctions;

    /**
     * Creates <tt>AutomatedNeuralNetwork</tt> object with given options
     *
     * @param activationFunctions available activation functions
     * @param data                training set
     * @param classifier          classifier object
     */
    public AutomatedNeuralNetwork(List<AbstractFunction> activationFunctions, Instances data,
                                  NeuralNetwork classifier) {
        super(data, classifier);
        this.setActivationFunctions(activationFunctions);
    }

    /**
     * Returns available activation functions.
     *
     * @return available activation functions
     */
    public final List<AbstractFunction> getActivationFunctions() {
        return activationFunctions;
    }

    /**
     * Sets available activation functions.
     *
     * @param activationFunctions available activation functions
     */
    public final void setActivationFunctions(List<AbstractFunction> activationFunctions) {
        this.activationFunctions = activationFunctions;
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new AutomatedNetworkBuilder();
    }

    /**
     *
     */
    private class AutomatedNetworkBuilder implements IterativeExperiment {

        int index;

        AutomatedNetworkBuilder() {
            clearHistory();
        }

        @Override
        public ClassifierDescriptor next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            NeuralNetwork model = (NeuralNetwork) AbstractClassifier.makeCopy(classifier);

            AbstractFunction randomActivationFunction = activationFunctions.get(r.nextInt(activationFunctions.size()));
            AbstractFunction cloneFunction = randomActivationFunction.clone();
            double coefficientValue = NumberGenerator.random(MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
            cloneFunction.setCoefficient(coefficientValue);
            model.network().setActivationFunction(cloneFunction);

            model.network().setHiddenLayer(NeuralNetworkUtil.generateRandomHiddenLayer(getData()));

            ClassifierDescriptor classifierDescriptor = evaluateModel(model);
            ++index;
            return classifierDescriptor;
        }

        @Override
        public boolean hasNext() {
            return index < getNumIterations();
        }

        @Override
        public int getPercent() {
            return index * 100 / getNumIterations();
        }
    }

}
