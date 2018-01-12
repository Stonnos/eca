/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.core.DecimalFormatHandler;
import eca.core.InstancesHandler;
import eca.core.ListOptionsHandler;
import eca.core.MinMaxNormalizer;
import eca.core.evaluation.Evaluation;
import eca.ensemble.Iterable;
import eca.ensemble.IterativeBuilder;
import eca.filter.MissingValuesFilter;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import eca.text.NumericFormat;
import org.springframework.util.Assert;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class for generating neural network for classification task.
 *
 * @author Roman Batygin
 */
public class NeuralNetwork extends AbstractClassifier implements Iterable, InstancesHandler,
        ListOptionsHandler, DecimalFormatHandler {

    private static final int NIN_NEURONS_NUM_IN_HIDDEN_LAYER = 1;

    private static final DecimalFormat COMMON_DECIMAL_FORMAT = NumericFormat.getInstance(Integer.MAX_VALUE);

    /**
     * Initial training set
     **/
    private Instances data;

    /**
     * Multilayer perceptron
     **/
    private MultilayerPerceptron network;

    /**
     * Decimal format.
     */
    private DecimalFormat decimalFormat = NumericFormat.getInstance();

    private MinMaxNormalizer normalizer;
    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Creates <tt>NeuralNetwork</tt> object and sets the initial
     * values for input and output neurons number.
     *
     * @param data training data
     */
    public NeuralNetwork(Instances data) {
        Assert.notNull(data, "Instances is not specified!");
        this.network = new MultilayerPerceptron(data.numAttributes() - 1, data.numClasses());
        initializeInputOptions(data);
    }

    /**
     * Default constructor for creation <tt>NeuralNetwork</tt> object.
     */
    public NeuralNetwork() {
        this.network = new MultilayerPerceptron(MultilayerPerceptron.MINIMUM_NUMBER_OF_NEURONS_IN_LAYER,
                MultilayerPerceptron.MINIMUM_NUMBER_OF_NEURONS_IN_LAYER);
    }

    /**
     * Creates <tt>NeuralNetwork</tt> object with given options.
     *
     * @param data     training data
     * @param function the neurons activation function in hidden layer
     */
    public NeuralNetwork(Instances data, ActivationFunction function) {
        this(data);
        network.setActivationFunction(function);
    }

    @Override
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public String[] getOptions() {
        List<String> options = getListOptions();
        return options.toArray(new String[options.size()]);
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = new ArrayList<>();

        options.add(NeuralNetworkDictionary.IN_LAYER_NEURONS_NUM);
        options.add(String.valueOf(network().inLayerNeuronsNum()));
        options.add(NeuralNetworkDictionary.OUT_LAYER_NEURONS_NUM);
        options.add(String.valueOf(network().outLayerNeuronsNum()));
        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_NUM);
        options.add(String.valueOf(network().hiddenLayersNum()));
        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_STRUCTURE);
        options.add(network().getHiddenLayer());
        options.add(NeuralNetworkDictionary.MAX_ITS);
        options.add(String.valueOf(network().getMaxIterationsNum()));
        options.add(NeuralNetworkDictionary.ERROR_THRESHOLD);
        options.add(COMMON_DECIMAL_FORMAT.format(network().getMinError()));

        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_AF);
        ActivationFunction activationFunction = network().getActivationFunction();
        options.add(activationFunction.getActivationFunctionType().getDescription());

        if (activationFunction instanceof AbstractFunction) {
            options.add(NeuralNetworkDictionary.HIDDEN_LAYER_AF_FORMULA);
            fillActivationFunctionOptions(activationFunction, options);
        }

        options.add(NeuralNetworkDictionary.OUT_LAYER_AF);
        ActivationFunction outActivationFunction = network().getOutActivationFunction();
        options.add(outActivationFunction.getActivationFunctionType().getDescription());

        if (outActivationFunction instanceof AbstractFunction) {
            options.add(NeuralNetworkDictionary.OUT_LAYER_AF_FORMULA);
            fillActivationFunctionOptions(outActivationFunction, options);
        }

        options.add(NeuralNetworkDictionary.LEARNING_ALGORITHM);
        options.add(network().getLearningAlgorithm().getClass().getSimpleName());

        if (network.getLearningAlgorithm() instanceof BackPropagation) {
            String[] algorithmOptions = network().getLearningAlgorithm().getOptions();
            for (int i = 0; i < algorithmOptions.length; i++) {
                options.add(algorithmOptions[i]);
            }
        }

        return options;
    }

    /**
     * Returns <tt>MultilayerPerceptron</tt> object.
     *
     * @return <tt>MultilayerPerceptron</tt> object
     */
    public MultilayerPerceptron network() {
        return network;
    }

    @Override
    public Instances getData() {
        return data;
    }

    /**
     * Builds neural network structure.
     */
    public void buildNetwork() {
        network.build();
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new NetworkBuilder(data);
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        initialize(data);
        double[][] x = normalizer.normalizeInputValues();
        double[][] y = normalizer.normalizeOutputValues();
        network.train(x, y);
    }

    @Override
    public double classifyInstance(Instance obj) {
        double[] x = normalizer.normalizeObject(filter.filterInstance(obj));
        double[] y = network.computeOutputVector(x);
        return classValue(y);
    }

    @Override
    public double[] distributionForInstance(Instance obj) {
        double[] x = normalizer.normalizeObject(filter.filterInstance(obj));
        double[] y = network.computeOutputVector(x);
        if (Utils.eq(Utils.sum(y), 0)) {
            return y;
        } else {
            Utils.normalize(y);
        }
        return y;
    }

    private void fillActivationFunctionOptions(ActivationFunction activationFunction, List<String> options) {
        AbstractFunction abstractFunction = (AbstractFunction) activationFunction;
        if (abstractFunction.getCoefficient() != 1.0) {
            options.add(String.format(abstractFunction.getActivationFunctionType().getFormulaFormat(),
                    getDecimalFormat().format(abstractFunction.getCoefficient())));
        } else {
            options.add(abstractFunction.getActivationFunctionType().getFormula());
        }
    }

    private void initialize(Instances data) throws Exception {
        this.data = data;
        initializeInputOptions(data);
        normalizer = new MinMaxNormalizer(filter.filterInstances(data));
        buildNetwork();
    }

    private void initializeInputOptions(Instances data) {
        network.setInLayerNeuronsNum(data.numAttributes() - 1);
        network.setOutLayerNeuronsNum(data.numClasses());
        if (network.getHiddenLayer() == null) {
            int neuronsNum = NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data);
            if (neuronsNum < NIN_NEURONS_NUM_IN_HIDDEN_LAYER) {
                neuronsNum = NIN_NEURONS_NUM_IN_HIDDEN_LAYER;
            }
            network.setHiddenLayer(String.valueOf(neuronsNum));
        }
    }

    private double classValue(double[] y) {
        double classValue = 0.0;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            if (y[i] > max) {
                max = y[i];
                classValue = i;
            }
        }
        return classValue;
    }

    /**
     * Neural network iterative builder.
     */
    private class NetworkBuilder extends IterativeBuilder {

        MultilayerPerceptron.IterativeBuilder iterativeBuilder;
        Evaluation evaluation;

        NetworkBuilder(Instances data) throws Exception {
            initialize(data);
            double[][] x = normalizer.normalizeInputValues();
            double[][] y = normalizer.normalizeOutputValues();
            iterativeBuilder = network().getIterativeBuilder(x, y);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index = iterativeBuilder.next();
            step = iterativeBuilder.step();
            return index;
        }

        @Override
        public int numIterations() {
            return network().getMaxIterationsNum();
        }

        @Override
        public Evaluation evaluation() throws Exception {
            if (evaluation == null) {
                evaluation = evaluateModel(NeuralNetwork.this, data);
            }
            return evaluation;
        }

        @Override
        public boolean hasNext() {
            return iterativeBuilder.isNext();
        }
    }

}
