/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.core.DecimalFormatHandler;
import eca.core.FilterHandler;
import eca.core.InstancesHandler;
import eca.core.ListOptionsHandler;
import eca.core.MinMaxNormalizer;
import eca.core.evaluation.Evaluation;
import eca.ensemble.Iterable;
import eca.ensemble.IterativeBuilder;
import eca.filter.MissingValuesFilter;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import eca.neural.functions.ActivationFunctionsDictionary;
import eca.text.NumericFormatFactory;
import eca.util.Utils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Randomizable;

import java.text.DecimalFormat;
import java.util.*;

import static eca.neural.NeuralNetworkDictionary.BACK_PROPAGATION_METHOD_TEXT;

/**
 * Class for generating neural network for classification task.
 *
 * @author Roman Batygin
 */
public class NeuralNetwork extends AbstractClassifier implements Iterable, InstancesHandler,
        ListOptionsHandler, DecimalFormatHandler, Randomizable, FilterHandler {

    private static final DecimalFormat COMMON_DECIMAL_FORMAT = NumericFormatFactory.getInstance(Integer.MAX_VALUE);

    /**
     * Initial training set
     **/
    private Instances data;

    /**
     * Multilayer perceptron
     **/
    private MultilayerPerceptron multilayerPerceptron;

    /**
     * Seed value for random generator
     */
    private int seed;

    /**
     * Decimal format.
     */
    private DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private MinMaxNormalizer normalizer;
    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Creates <tt>NeuralNetwork</tt> object and sets the initial
     * values for input and output neurons number.
     *
     * @param data training data
     */
    public NeuralNetwork(Instances data) {
        Objects.requireNonNull(data, "Instances is not specified!");
        initializeInputOptions(data);
    }

    /**
     * Default constructor for creation <tt>NeuralNetwork</tt> object.
     */
    public NeuralNetwork() {
        this.multilayerPerceptron = new MultilayerPerceptron(MultilayerPerceptron.MINIMUM_NUMBER_OF_NEURONS_IN_LAYER,
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
        this.multilayerPerceptron.setActivationFunction(function);
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
    public MissingValuesFilter getFilter() {
        return filter;
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = new ArrayList<>();

        options.add(NeuralNetworkDictionary.IN_LAYER_NEURONS_NUM);
        options.add(String.valueOf(getMultilayerPerceptron().getNumInNeurons()));
        options.add(NeuralNetworkDictionary.OUT_LAYER_NEURONS_NUM);
        options.add(String.valueOf(getMultilayerPerceptron().getNumOutNeurons()));
        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_NUM);
        options.add(String.valueOf(getMultilayerPerceptron().hiddenLayersNum()));
        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_STRUCTURE);
        options.add(getMultilayerPerceptron().getHiddenLayer());
        options.add(NeuralNetworkDictionary.MAX_ITS);
        options.add(String.valueOf(getMultilayerPerceptron().getNumIterations()));
        options.add(NeuralNetworkDictionary.ERROR_THRESHOLD);
        options.add(COMMON_DECIMAL_FORMAT.format(getMultilayerPerceptron().getMinError()));

        options.add(NeuralNetworkDictionary.HIDDEN_LAYER_AF);
        ActivationFunction activationFunction = getMultilayerPerceptron().getActivationFunction();
        options.add(activationFunction.getActivationFunctionType().getDescription());

        if (activationFunction instanceof AbstractFunction) {
            options.add(NeuralNetworkDictionary.HIDDEN_LAYER_AF_FORMULA);
            fillActivationFunctionOptions(activationFunction, options);
        }

        options.add(NeuralNetworkDictionary.OUT_LAYER_AF);
        ActivationFunction outActivationFunction = getMultilayerPerceptron().getOutActivationFunction();
        options.add(outActivationFunction.getActivationFunctionType().getDescription());

        if (outActivationFunction instanceof AbstractFunction) {
            options.add(NeuralNetworkDictionary.OUT_LAYER_AF_FORMULA);
            fillActivationFunctionOptions(outActivationFunction, options);
        }

        options.add(NeuralNetworkDictionary.LEARNING_ALGORITHM);
        options.add(BACK_PROPAGATION_METHOD_TEXT);

        if (multilayerPerceptron.getLearningAlgorithm() instanceof BackPropagation) {
            String[] algorithmOptions = getMultilayerPerceptron().getLearningAlgorithm().getOptions();
            options.addAll(Arrays.asList(algorithmOptions));
        }

        options.add(NeuralNetworkDictionary.SEED);
        options.add(String.valueOf(seed));

        return options;
    }

    /**
     * Returns <tt>MultilayerPerceptron</tt> object.
     *
     * @return <tt>MultilayerPerceptron</tt> object
     */
    public MultilayerPerceptron getMultilayerPerceptron() {
        return multilayerPerceptron;
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Builds neural network structure.
     */
    public void buildNetwork() {
        multilayerPerceptron.build();
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
        multilayerPerceptron.train(x, y);
    }

    @Override
    public double classifyInstance(Instance obj) {
        double[] x = normalizer.normalizeObject(filter.filterInstance(obj));
        double[] y = multilayerPerceptron.computeOutputVector(x);
        return classValue(y);
    }

    @Override
    public double[] distributionForInstance(Instance obj) {
        double[] x = normalizer.normalizeObject(filter.filterInstance(obj));
        double[] y = multilayerPerceptron.computeOutputVector(x);
        Utils.normalize(y);
        return y;
    }

    private void fillActivationFunctionOptions(ActivationFunction activationFunction, List<String> options) {
        AbstractFunction abstractFunction = (AbstractFunction) activationFunction;
        if (abstractFunction.getCoefficient() != ActivationFunctionsDictionary.DEFAULT_COEFFICIENT) {
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
        if (multilayerPerceptron == null) {
            multilayerPerceptron = new MultilayerPerceptron(data.numAttributes() - 1, data.numClasses());
        } else {
            multilayerPerceptron.setNumInNeurons(data.numAttributes() - 1);
            multilayerPerceptron.setNumOutNeurons(data.numClasses());
        }
        if (multilayerPerceptron.getHiddenLayer() == null) {
            multilayerPerceptron.setHiddenLayer(String.valueOf(NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(data)));
        }
        multilayerPerceptron.getLearningAlgorithm().getRandom().setSeed(seed);
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
            iterativeBuilder = getMultilayerPerceptron().getIterativeBuilder(x, y);
        }

        @Override
        public int next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index = iterativeBuilder.next();
            return index;
        }

        @Override
        public int numIterations() {
            return getMultilayerPerceptron().getNumIterations();
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
