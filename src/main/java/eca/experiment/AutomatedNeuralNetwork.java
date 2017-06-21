/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import eca.neural.functions.ActivationFunction;
import weka.core.Instances;

import java.util.NoSuchElementException;

import eca.beans.ClassifierDescriptor;
import eca.neural.NeuralNetwork;
import weka.classifiers.AbstractClassifier;

/**
 *
 * @author Roman93
 */
public class AutomatedNeuralNetwork extends AbstractExperiment<NeuralNetwork> {

    private ActivationFunction[] activationFunctions;

    public AutomatedNeuralNetwork(int numExperiments, ActivationFunction[] activationFunctions, Instances data, NeuralNetwork classifier) {
        super(data, classifier, numExperiments);
        this.setNumIterations(numExperiments);
        this.setActivationFunctions(activationFunctions);
    }

    public final ActivationFunction[] getActivationFunctions() {
        return activationFunctions;
    }

    public final void setActivationFunctions(ActivationFunction[] activationFunctions) {
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
            model.network().setActivationFunction(activationFunctions[r.nextInt(activationFunctions.length)]);
            model.setRandomHiddenLayer(r);
            ++index;
            return evaluateModel(model);
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
