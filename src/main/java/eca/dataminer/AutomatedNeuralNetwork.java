/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.model.ClassifierDescriptor;
import eca.neural.NeuralNetwork;
import eca.neural.functions.ActivationFunction;
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

    /**
     * Available activation functions
     **/
    private List<ActivationFunction> activationFunctions;

    /**
     * Creates <tt>AutomatedNeuralNetwork</tt> object with given options
     *
     * @param activationFunctions available activation functions
     * @param data                training set
     * @param classifier          classifier object
     */
    public AutomatedNeuralNetwork(List<ActivationFunction> activationFunctions, Instances data,
                                  NeuralNetwork classifier) {
        super(data, classifier);
        this.setActivationFunctions(activationFunctions);
    }

    /**
     * Returns available activation functions.
     *
     * @return available activation functions
     */
    public final List<ActivationFunction> getActivationFunctions() {
        return activationFunctions;
    }

    /**
     * Sets available activation functions.
     *
     * @param activationFunctions available activation functions
     */
    public final void setActivationFunctions(List<ActivationFunction> activationFunctions) {
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
            ActivationFunction activationFunction = activationFunctions.get(r.nextInt(activationFunctions.size()));
            model.network().setActivationFunction(activationFunction);
            model.setRandomHiddenLayer();
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
