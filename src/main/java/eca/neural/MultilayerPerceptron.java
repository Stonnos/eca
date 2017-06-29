/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import eca.neural.functions.ActivationFunction;
import eca.neural.functions.LogisticFunction;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Class for generation neural network of type multilayer perceptron. <p>
 *
 * Valid options are: <p>
 *
 * Sets the number of neurons in input layer <p>
 *
 * Sets the number of neurons in output layer <p>
 *
 * Sets the hidden layer structure <p>
 *
 * Sets the neurons activation function in hidden layer <p>
 *
 * Sets the neurons activation function in output layer (Default: {@link LogisticFunction}) <p>
 *
 * Sets the learning algorithm (Default: {@link BackPropagation}) <p>
 *
 * Sets the maximum number of iterations for learning (Default: 1000000) <p>
 *
 * Sets the value of minimum error threshold (Default: 0.00001) <p>
 *
 * @author Рома
 */
public class MultilayerPerceptron implements java.io.Serializable {

    /** Input neurons **/
    protected Neuron[] inLayerNeurons;

    /** Hidden neurons **/
    protected Neuron[][] hiddenLayerNeurons;

    /** Output neurons **/
    protected Neuron[] outLayerNeurons;

    /** Neurons activation function in hidden layer **/
    private ActivationFunction hiddenFunction;

    /** Neurons activation function in output layer **/
    private ActivationFunction outerFunction = new LogisticFunction();

    /** Learning algorithm **/
    private LearningAlgorithm algorithm = new BackPropagation(this);

    /** Number of neurons in input layer **/
    private int inLayerNeuronsNum;

    /** Number of neurons in output layer **/
    private int outLayerNeuronsNum;

    /** Hidden layer structure **/
    private String hiddenLayer;

    /** Number of hidden layers **/
    private int hiddenLayersNum = 1;

    /** The value of minimum error threshold **/
    private double minError = 0.00001;

    /** The maximum number of iterations for learning **/
    private int maxIterationsNum = 1000000;

    /** Number of neural links **/
    private int linksNum;

    /**
     * Creates <tt>MultilayerPerceptron</tt> with given options.
     * @param inLayerNeuronsNum the number of neurons in input layer
     * @param outLayerNeuronsNum the number of neurons in output layer
     * @param function the neurons activation function in hidden layer
     */
    public MultilayerPerceptron(int inLayerNeuronsNum, int outLayerNeuronsNum,
            ActivationFunction function) {
        this.setInLayerNeuronsNum(inLayerNeuronsNum);
        this.setOutLayerNeuronsNum(outLayerNeuronsNum);
        this.setActivationFunction(function);
    }

    /**
     * Creates <tt>MultilayerPerceptron</tt> with given options.
     * @param inLayerNeuronsNum the number of neurons in input layer
     * @param outLayerNeuronsNum the number of neurons in output layer
     */
    public MultilayerPerceptron(int inLayerNeuronsNum, int outLayerNeuronsNum) {
        this(inLayerNeuronsNum, outLayerNeuronsNum, new LogisticFunction());
    }

    /**
     * Returns the hidden layer structure.
     * @return the hidden layer structure
     */
    public String getHiddenLayer() {
        return hiddenLayer;
    }

    /**
     * Sets the hidden layer structure.
     * @param hiddenLayer the hidden layer structure
     * @exception IllegalArgumentException if the hidden layer structure is invalid
     */
    public void setHiddenLayer(String hiddenLayer) {
        hiddenLayersNum = 0;
        StringTokenizer tokenizer = new StringTokenizer(hiddenLayer, ",");
        if (!tokenizer.hasMoreElements()) {
            throw new IllegalArgumentException("Количество скрытых слоев должно быть не менее одного!");
        }
        while (tokenizer.hasMoreTokens()) {
            String str = tokenizer.nextToken();
            if (!str.matches("^[0-9]*$")) {
                throw new IllegalArgumentException("Неправильный формат структуры скрытого слоя!");
            }
            if (Integer.valueOf(str).equals(0)) {
                throw new IllegalArgumentException("Количество нейронов в одном скрытом слое должно быть не менее одного!");
            }
            hiddenLayersNum++;
        }
        this.hiddenLayer = hiddenLayer;
    }

    /**
     * Returns the number of neurons in input layer.
     * @return the number of neurons in input layer
     */
    public int inLayerNeuronsNum() {
        return inLayerNeuronsNum;
    }

    /**
     * Returns the number of neurons in output layer.
     * @return the number of neurons in output layer
     */
    public int outLayerNeuronsNum() {
        return outLayerNeuronsNum;
    }

    /**
     * Returns the number of hidden layers.
     * @return the number of hidden layers
     */
    public int hiddenLayersNum() {
        return hiddenLayersNum;
    }

    /**
     * Sets the number of neurons in input layer.
     * @param inLayerNeuronsNum the number of neurons in input layer
     * @exception IllegalArgumentException if the the number of neurons in input layer
     * is less than 1
     */
    public final void setInLayerNeuronsNum(int inLayerNeuronsNum) {
        checkValue(inLayerNeuronsNum);
        this.inLayerNeuronsNum = inLayerNeuronsNum;
    }

    /**
     * Sets the number of neurons in output layer.
     * @param outLayerNeuronsNum the number of neurons in output layer
     * @exception IllegalArgumentException if the the number of neurons in output layer
     * is less than 1
     */
    public final void setOutLayerNeuronsNum(int outLayerNeuronsNum) {
        checkValue(outLayerNeuronsNum);
        this.outLayerNeuronsNum = outLayerNeuronsNum;
    }

    /**
     * Returns the number of neural links.
     * @return the number of neural links
     */
    public int getLinksNum() {
        return linksNum;
    }

    /**
     * Returns the common number of layers.
     * @return the common number of layers.
     */
    public int layersNum() {
        return hiddenLayersNum() + 2;
    }

    /**
     * Sets the value of minimum error threshold.
     * @param min_error the value of minimum error threshold
     */
    public void setMinError(double min_error) {
        this.minError = min_error;
    }

    /**
     * Return the value of the value of minimum error threshold.
     * @return the value of the value of minimum error threshold
     */
    public double getMinError() {
        return minError;
    }

    /**
     * Sets the maximum number of iterations for learning.
     * @param max_iterations_num the maximum number of iterations for learning
     */
    public void setMaxIterationsNum(int max_iterations_num) {
        this.maxIterationsNum = max_iterations_num;
    }

    /**
     * Returns the maximum number of iterations for learning.
     * @return the maximum number of iterations for learning
     */
    public int getMaxIterationsNum() {
        return maxIterationsNum;
    }

    /**
     * Returns the neurons activation function in hidden layer.
     * @return the neurons activation function in hidden layer
     */
    public ActivationFunction getActivationFunction() {
        return hiddenFunction;
    }

    /**
     * Sets the neurons activation function in output layer.
     * @return the neurons activation function in output layer
     */
    public ActivationFunction getOutActivationFunction() {
        return outerFunction;
    }

    /**
     * Sets the neurons activation function in hidden layer.
     * @param function the neurons activation function in hidden layer
     */
    public final void setActivationFunction(ActivationFunction function) {
        if (function == null) {
            throw new IllegalArgumentException();
        }
        this.hiddenFunction = function;
    }

    /**
     * Sets the neurons activation function in output layer.
     * @param function the neurons activation function in output layer
     */
    public final void setOutActivationFunction(ActivationFunction function) {
        if (function == null) {
            throw new IllegalArgumentException();
        }
        this.outerFunction = function;
    }

    /**
     * Sets the learning algorithm.
     * @param algorithm the learning algorithm object
     */
    public void setLearningAlgorithm(LearningAlgorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException();
        }
        if (algorithm.network != this) {
            throw new IllegalArgumentException();
        }
        this.algorithm = algorithm;
    }

    /**
     * Returns the learning algorithm.
     * @return the learning algorithm object
     */
    public LearningAlgorithm getLearningAlgorithm() {
        return algorithm;
    }

    /**
     * Builds the multilayer perceptron structure.
     */
    public void build() {
        inLayerNeurons = new Neuron[inLayerNeuronsNum];
        StringTokenizer tokenizer = new StringTokenizer(hiddenLayer, ",");
        hiddenLayerNeurons = new Neuron[hiddenLayersNum()][];
        for (int i = 0; i < hiddenLayerNeurons.length; i++) {
            hiddenLayerNeurons[i] = new Neuron[Integer.valueOf(tokenizer.nextToken())];
        }
        outLayerNeurons = new Neuron[outLayerNeuronsNum];
        createLayers();
        createLinks();
    }

    /**
     * Calculates the output vector.
     * @param x input vector
     * @return the output vector
     */
    public double[] computeOutputVector(double[] x) {
        checkVector(x, inLayerNeuronsNum());
        double[] y = new double[outLayerNeuronsNum()];
        for (int i = 0; i < inLayerNeuronsNum(); i++) {
            inLayerNeurons[i].setOutValue(x[i]);
        }
        computeValuesInHiddenLayer();
        for (int i = 0; i < outLayerNeuronsNum(); i++) {
            Neuron u = outLayerNeurons[i];
            u.sum();
            y[i] = u.process();
        }
        return y;
    }

    /**
     * Trains network with training data.
     * @param input input values
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
            if (error(y, output[j]) < minError || i > maxIterationsNum) {
                break;
            }
            algorithm.train(y, output[j]);
        }
    }

    /**
     * Returns an <tt>IterativeBuilder</tt> object.
     * @param input input values
     * @param output output values
     * @return
     */
    public IterativeBuilder getIterativeBuilder(double[][] input, double[][] output) {
        return new IterativeBuilder(input, output);
    }

    /**
     *
     */
    public class IterativeBuilder {

        private int i;
        private int step = 1;
        private final double[][] input;
        private final double[][] output;

        public IterativeBuilder(double[][] input, double[][] output) {
            checkInputVectors(input, output);
            this.input = input;
            this.output = output;
            algorithm.initializeWeights();
        }

        public int index() {
            return i;
        }

        public int step() {
            return step;
        }

        public int next() throws Exception {
            if (!isNext()) {
                throw new NoSuchElementException();
            }
            int j = i % input.length;
            double[] y = computeOutputVector(input[j]);
            if (error(y, output[j]) < minError || i > maxIterationsNum) {
                step = maxIterationsNum - i;
                i = maxIterationsNum - 1;
            } else {
                algorithm.train(y, output[j]);
            }
            return ++i;
        }

        public boolean isNext() {
            return i < getMaxIterationsNum();
        }

    }

    private void createLayers() {
        int index = 0;
        for (int i = 0; i < inLayerNeuronsNum(); i++) {
            inLayerNeurons[i] = new Neuron(index++, null, Neuron.IN_LAYER);
        }

        for (int i = 0; i < hiddenLayersNum(); i++) {
            for (int j = 0; j < hiddenLayerNeurons[i].length; j++) {
                hiddenLayerNeurons[i][j] = new Neuron(index++, hiddenFunction, Neuron.HIDDEN_LAYER);
            }
        }

        for (int i = 0; i < outLayerNeuronsNum(); i++) {
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
        if (value < 1) {
            throw new IllegalArgumentException("Число нейронов "
                    + "должно быть больше 1!");
        }
    }

    private void checkVector(double[] x, int size) {
        if (x == null) {
            throw new IllegalArgumentException();
        }
        if (x.length != size) {
            throw new IllegalArgumentException("Illegal value: "
                    + String.valueOf(x.length));
        }
    }

    private void checkInputVectors(double[][] input, double[][] output) {
        if (input == null || output == null) {
            throw new IllegalArgumentException();
        }
        if (input.length != output.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < input.length; i++) {
            checkVector(input[i], inLayerNeuronsNum());
            checkVector(output[i], outLayerNeuronsNum());
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

    private double error(double[] actual, double[] expected) {
        double error = 0.0;
        for (int i = 0; i < outLayerNeuronsNum(); i++) {
            error += Math.pow(actual[i] - expected[i], 2);
        }
        return 0.5 * error;
    }

}
