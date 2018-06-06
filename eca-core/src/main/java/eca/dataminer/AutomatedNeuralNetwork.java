/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.evaluation.EvaluationResults;
import eca.generators.NumberGenerator;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements automatic selection of optimal options
 * for neural networks based on experiment series.
 *
 * @author Roman Batygin
 */
public class AutomatedNeuralNetwork extends AbstractExperiment<NeuralNetwork> {

    private static final double MIN_COEFFICIENT_VALUE = 1.0;
    private static final double MAX_COEFFICIENT_VALUE = 5.0;

    /**
     * Available activation functions
     **/
    private static final ActivationFunctionType[] ACTIVATION_FUNCTIONS_TYPES = ActivationFunctionType.values();

    private static final ActivationFunctionBuilder ACTIVATION_FUNCTION_BUILDER = new ActivationFunctionBuilder();

    /**
     * Creates <tt>AutomatedNeuralNetwork</tt> object with given options
     *
     * @param data       training set
     * @param classifier classifier object
     */
    public AutomatedNeuralNetwork(Instances data, NeuralNetwork classifier) {
        super(data, classifier);
    }

    @Override
    public IterativeExperiment getIterativeExperiment() {
        return new AutomatedNetworkBuilder();
    }

    /**
     * Automated network builder.
     */
    private class AutomatedNetworkBuilder extends AbstractIterativeBuilder {

        @Override
        public EvaluationResults next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            incrementIndex();
            NeuralNetwork model = (NeuralNetwork) AbstractClassifier.makeCopy(getClassifier());
            model.setSeed(getSeed());
            ActivationFunctionType activationFunctionType =
                    ACTIVATION_FUNCTIONS_TYPES[random.nextInt(ACTIVATION_FUNCTIONS_TYPES.length)];
            AbstractFunction randomActivationFunction = activationFunctionType.handle(ACTIVATION_FUNCTION_BUILDER);
            double coefficientValue = NumberGenerator.random(MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
            randomActivationFunction.setCoefficient(coefficientValue);
            model.getMultilayerPerceptron().setActivationFunction(randomActivationFunction);
            model.getMultilayerPerceptron().setHiddenLayer(
                    NeuralNetworkUtil.generateRandomHiddenLayer(getData(), random));
            return evaluateModel(model);
        }
    }

}
