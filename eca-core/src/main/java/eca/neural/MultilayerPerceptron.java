/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.neural.functions.ActivationFunction;
import eca.neural.functions.LogisticFunction;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * Class for generation neural network of type multilayer perceptron. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Sets the number of neurons in input layer <p>
 * <p>
 * Sets the number of neurons in output layer <p>
 * <p>
 * Sets the hidden layer structure <p>
 * <p>
 * Sets the neurons activation function in hidden layer <p>
 * <p>
 * Sets the neurons activation function in output layer (Default: {@link LogisticFunction}) <p>
 * <p>
 * Sets the learning algorithm (Default: {@link BackPropagation}) <p>
 * <p>
 * Sets the maximum number of iterations for learning (Default: 1000000) <p>
 * <p>
 * Sets the value of minimum error threshold (Default: 0.00001) <p>
 *
 * @author Roman Batygin
 */
public class MultilayerPerceptron implements java.io.Serializable {

    private static final String LAYER_REGEX = "^[0-9]*$";

    public static final int MINIMUM_NUMBER_OF_NEURONS_IN_LAYER = 1;

    /**
     * Input neurons
     **/
    protected Neuron[] inLayerNeurons;

    /**
     * Hidden neurons
     **/
    protected Neuron[][] hiddenLayerNeurons;

    /**
     * Output neurons
     **/
    protected Neuron[] outLayerNeurons;

    /**
     * Neurons activation function in hidden layer
     **/
    private ActivationFunction hiddenFunction;

    /**
     * Neurons activation function in output layer
     **/
    private ActivationFunction outerFunction = new LogisticFunction();

    /**
     * Learning algorithm
     **/
    private LearningAlgorithm algorithm = new BackPropagation(this);

    /**
     * Number of neurons in input layer
     **/
    private int numInNeurons;

    /**
     * Number of neurons in output layer
     **/
    private int numOutNeurons;

    /**
     * Hidden layer structure
     **/
    private String hiddenLayer;

    /**
     * Number of hidden layers
     **/
    private int hiddenLayersNum = 1;

    /**
     * The value of minimum error threshold
     **/
    private double minError = 0.00001;

    /**
     * The maximum number of iterations for learning
     **/
    private int numIterations = 1000000;

    /**
     * Number of neural links
     **/
    private int linksNum;

    /**
     * Creates <tt>MultilayerPerceptron</tt> with given options.
     *
     * @param numInNeurons  the number of neurons in input layer
     * @param numOutNeurons the number of neurons in output layer
     * @param function           the neurons activation function in hidden layer
     */
    public MultilayerPerceptron(int numInNeurons, int numOutNeurons,
                                ActivationFunction function) {
        this.setNumInNeurons(numInNeurons);
        this.setNumOutNeurons(numOutNeurons);
        this.setActivationFunction(function);
    }

    /**
     * Creates <tt>MultilayerPerceptron</tt> with given options.
     *
     * @param numInNeurons  the number of neurons in input layer
     * @param numOutNeurons the number of neurons in output layer
     */
    public MultilayerPerceptron(int numInNeurons, int numOutNeurons) {
        this(numInNeurons, numOutNeurons, new LogisticFunction());
    }

    /**
     * Returns the hidden layer structure.
     *
     * @return the hidden layer structure
     */
    public String getHiddenLayer() {
        return hiddenLayer;
    }

    /**
     * Sets the hidden layer structure.
     *
     * @param hiddenLayer the hidden layer structure
     * @throws IllegalArgumentException if the hidden layer structure is invalid
     */
    public void setHiddenLayer(String hiddenLayer) {
        hiddenLayersNum = 0;
        StringTokenizer tokenizer = new StringTokenizer(hiddenLayer, ",");
        if (!tokenizer.hasMoreElements()) {
            throw new IllegalArgumentException(NeuralNetworkDictionary.BAD_HIDDEN_LAYERS_NUM_ERROR_TEXT);
        }
        while (tokenizer.hasMoreTokens()) {
            String str = tokenizer.nextToken();
            if (!str.matches(LAYER_REGEX)) {
                throw new IllegalArgumentException(NeuralNetworkDictionary.BAD_HIDDEN_LAYER_STRUCTURE);
            }
            if (Integer.valueOf(str).equals(0)) {
                throw new IllegalArgumentException(NeuralNetworkDictionary.BAD_NEURONS_NUM_IN_HIDDEN_LAYER_ERROR_TEXT);
            }
            hiddenLayersNum++;
        }
        this.hiddenLayer = hiddenLayer;
    }

    /**
     * Returns the number of neurons in input layer.
     *
     * @return the number of neurons in input layer
     */
    public int getNumInNeurons() {
        return numInNeurons;
    }

    /**
     * Returns the number of neurons in output layer.
     *
     * @return the number of neurons in output layer
     */
    public int getNumOutNeurons() {
        return numOutNeurons;
    }

    /**
     * Returns the number of hidden layers.
     *
     * @return the number of hidden layers
     */
    public int hiddenLayersNum() {
        return hiddenLayersNum;
    }

    /**
     * Sets the number of neurons in input layer.
     *
     * @param numInNeurons the number of neurons in input layer
     * @throws IllegalArgumentException if the the number of neurons in input layer
     *                                  is less than 1
     */
    public final void setNumInNeurons(int numInNeurons) {
        checkValue(numInNeurons);
        this.numInNeurons = numInNeurons;
    }

    /**
     * Sets the number of neurons in output layer.
     *
     * @param numOutNeurons the number of neurons in output layer
     * @throws IllegalArgumentException if the the number of neurons in output layer
     *                                  is less than 1
     */
    public final void setNumOutNeurons(int numOutNeurons) {
        checkValue(numOutNeurons);
        this.numOutNeurons = numOutNeurons;
    }

    /**
     * Returns the number of neural links.
     *
     * @return the number of neural links
     */
    public int getLinksNum() {
        return linksNum;
    }

    /**
     * Returns the common number of layers.
     *
     * @return the common number of layers.
     */
    public int layersNum() {
        return hiddenLayersNum() + 2;
    }

    /**
     * Sets the value of minimum error threshold.
     *
     * @param minError the value of minimum error threshold
     */
    public void setMinError(double minError) {
        this.minError = minError;
    }

    /**
     * Return the value of the value of minimum error threshold.
     *
     * @return the value of the value of minimum error threshold
     */
    public double getMinError() {
        return minError;
    }

    /**
     * Sets the maximum number of iterations for learning.
     *
     * @param numIterations the maximum number of iterations for learning
     */
    public void setNumIterations(int numIterations) {
        this.numIterations = numIterations;
    }

    /**
     * Returns the maximum number of iterations for learning.
     *
     * @return the maximum number of iterations for learning
     */
    public int getNumIterations() {
        return numIterations;
    }

    /**
     * Returns the neurons activation function in hidden layer.
     *
     * @return the neurons activation function in hidden layer
     */
    public ActivationFunction getActivationFunction() {
        return hiddenFunction;
    }

    /**
     * Sets the neurons activation function in output layer.
     *
     * @return the neurons activation function in output layer
     */
    public ActivationFunction getOutActivationFunction() {
        return outerFunction;
    }

    /**
     * Sets the neurons activation function in hidden layer.
     *
     * @param function the neurons activation function in hidden layer
     */
    public final void setActivationFunction(ActivationFunction function) {
        Objects.requireNonNull(function, "Activation function is not specified!");
        this.hiddenFunction = function;
    }

    /**
     * Sets the neurons activation function in output layer.
     *
     * @param function the neurons activation function in output layer
     */
    public final void setOutActivationFunction(ActivationFunction function) {
        Objects.requireNonNull(function, "Activation function is not specified!");
        this.outerFunction = function;
    }

    /**
     * Sets the learning algorithm.
     *
     * @param algorithm the learning algorithm object
     */
    public void setLearningAlgorithm(LearningAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "Learning algorithm is not specified!");
        if (algorithm.network != this) {
            throw new IllegalArgumentException("Unexpected network reference!");
        }
        this.algorithm = algorithm;
    }

    /**
     * Returns the learning algorithm.
     *
     * @return the learning algorithm object
     */
    public LearningAlgorithm getLearningAlgorithm() {
        return algorithm;
    }

    /**
     * Builds the multilayer perceptron structure.
     */
    public void build() {
        inLayerNeurons = new Neuron[numInNeurons];
        StringTokenizer tokenizer = new StringTokenizer(hiddenLayer, ",");
        hiddenLayerNeurons = new Neuron[hiddenLayersNum()][];
        for (int i = 0; i < hiddenLayerNeurons.length; i++) {
            hiddenLayerNeurons[i] = new Neuron[Integer.valueOf(tokenizer.nextToken())];
        }
        outLayerNeurons = new Neuron[numOutNeurons];
        createLayers();
        createLinks();
    }

    /**
     * Calculates the output vector.
     *
     * @param x input vector
     * @return the output vector
     */
    public double[] computeOutputVector(double[] x) {
        checkVector(x, getNumInNeurons());
        double[] y = new double[getNumOutNeurons()];
        for (int i = 0; i < getNumInNeurons(); i++) {
            inLayerNeurons[i].setOutValue(x[i]);
        }
        computeValuesInHiddenLayer();
        for (int i = 0; i < getNumOutNeurons(); i++) {
            Neuron u = outLayerNeurons[i];
            u.sum();
            y[i] = u.process();
        }
        return y;
    }

    /**
     * Trains network with training data.
     *
     * @param input  input values
     * @param output output values
     * @throws Exception
     */
    public void train(double[][] input, double[][] output) throws Exception {
        checkInputVectors(input, output);
        int i = 0;
        algorithm.initializeWeights();
        while (true) {
            i++;
            int j = i % input.length;
            double[] y = computeOutputVector(input[j]);
            if (NeuralNetworkUtil.error(y, output[j]) < minError || i > numIterations) {
                break;
            }
            algorithm.train(y, output[j]);
        }
    }

    /**
     * Returns an <tt>IterativeBuilder</tt> object.
     *
     * @param input  input values
     * @param output output values
     * @return
     */
    public IterativeBuilder getIterativeBuilder(double[][] input, double[][] output) {
        return new IterativeBuilder(input, output);
    }

    /**
     * Multilayer perceptron iterative builder.
     */
    public class IterativeBuilder {

        private int i;
        private int step = 1;
        private final double[][] input;
        private final double[][] output;

        /**
         * Creates <tt>IterativeBuilder</tt> object.
         *
         * @param input  input vector
         * @param output output vector
         */
        public IterativeBuilder(double[][] input, double[][] output) {
            checkInputVectors(input, output);
            this.input = input;
            this.output = output;
            algorithm.initializeWeights();
        }

        /**
         * Returns the current number of iteration.
         *
         * @return the current number of iteration
         */
        public int index() {
            return i;
        }

        /**
         * Returns the value of step between iterations.
         *
         * @return the value of step between iterations
         */
        public int step() {
            return step;
        }

        /**
         * Performs the next iteration and returns its number.
         *
         * @return the number of next iteration.
         * @throws Exception
         */
        public int next() throws Exception {
            if (!isNext()) {
                throw new NoSuchElementException();
            }
            int j = i % input.length;
            double[] y = computeOutputVector(input[j]);
            if (NeuralNetworkUtil.error(y, output[j]) < minError || i > numIterations) {
                step = numIterations - i;
                i = numIterations - 1;
            } else {
                algorithm.train(y, output[j]);
            }
            return ++i;
        }

        /**
         * Returns <tt>true</tt> if the next iteration is exists.
         *
         * @return <tt>true</tt> if the next iteration is exists.
         */
        public boolean isNext() {
            return i < getNumIterations();
        }

    }

    private void createLayers() {
        int index = 0;
        for (int i = 0; i < getNumInNeurons(); i++) {
            inLayerNeurons[i] = new Neuron(index++, null, Neuron.IN_LAYER);
        }

        for (int i = 0; i < hiddenLayersNum(); i++) {
            for (int j = 0; j < hiddenLayerNeurons[i].length; j++) {
                hiddenLayerNeurons[i][j] = new Neuron(index++, hiddenFunction, Neuron.HIDDEN_LAYER);
            }
        }

        for (int i = 0; i < getNumOutNeurons(); i++) {
            outLayerNeurons[i] = new Neuron(index++, outerFunction, Neuron.OUT_LAYER);
        }
    }

    private void createLinks() {
        createInputLinks();
        createHiddenLinks();
        createOutLinks();
    }

    private void addLink(Neuron u, Neuron v) {
        NeuralLink link = new NeuralLink(u, v);
        u.addOutLink(link);
        v.addInLink(link);
        linksNum++;
    }

    private void createHiddenLinks() {
        for (int i = 0; i < hiddenLayersNum() - 1; i++) {
            for (Neuron u : hiddenLayerNeurons[i]) {
                for (Neuron v : hiddenLayerNeurons[i + 1]) {
                    addLink(u, v);
                }
            }
        }
    }

    private void createInputLinks() {
        for (Neuron u : inLayerNeurons) {
            for (Neuron v : hiddenLayerNeurons[0]) {
                addLink(u, v);
            }
        }
    }

    private void createOutLinks() {
        for (Neuron u : hiddenLayerNeurons[hiddenLayersNum() - 1]) {
            for (Neuron v : outLayerNeurons) {
                addLink(u, v);
            }
        }
    }

    private void checkValue(int value) {
        if (value < MINIMUM_NUMBER_OF_NEURONS_IN_LAYER) {
            throw new IllegalArgumentException(
                    String.format(NeuralNetworkDictionary.BAD_NEURONS_NUM_ERROR_FORMAT, MINIMUM_NUMBER_OF_NEURONS_IN_LAYER));
        }
    }

    private void checkVector(double[] x, int size) {
        Objects.requireNonNull(x, "Vector is not specified!");
        if (x.length != size) {
            throw new IllegalArgumentException(String.format("Illegal value: %d", x.length));
        }
    }

    private void checkInputVectors(double[][] input, double[][] output) {
        Objects.requireNonNull(input, "Input vector is not specified!");
        Objects.requireNonNull(input, "Output vector is not specified!");
        if (input.length != output.length) {
            throw new IllegalArgumentException("Input and output vectors must have the same lengths!");
        }
        for (int i = 0; i < input.length; i++) {
            checkVector(input[i], getNumInNeurons());
            checkVector(output[i], getNumOutNeurons());
        }
    }

    private void computeValuesInHiddenLayer() {
        for (int i = 0; i < hiddenLayersNum(); i++) {
            for (Neuron u : hiddenLayerNeurons[i]) {
                u.sum();
                u.process();
            }
        }
    }

}
