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
 *
 * @author Рома
 */
public class MultilayerPerceptron implements java.io.Serializable {

    protected Neuron[] inLayerNeurons;
    protected Neuron[][] hiddenLayerNeurons;
    protected Neuron[] outLayerNeurons;
    private ActivationFunction hiddenFunction;
    private ActivationFunction outerFunction = new LogisticFunction();
    private LearningAlgorithm algorithm = new BackPropagation(this);
    private int inLayerNeuronsNum;
    private int outLayerNeuronsNum;
    private int linksNum;
    private int hiddenLayersNum = 1;
    private double minError = 0.00001;
    private int maxIterationsNum = 1000000;
    private String hiddenLayer;

    public MultilayerPerceptron(int inLayerNeuronsNum, int outLayerNeuronsNum,
            ActivationFunction function) {
        this.setInLayerNeuronsNum(inLayerNeuronsNum);
        this.setOutLayerNeuronsNum(outLayerNeuronsNum);
        this.setActivationFunction(function);
    }

    public MultilayerPerceptron(int inLayerNeuronsNum, int outLayerNeuronsNum) {
        this(inLayerNeuronsNum, outLayerNeuronsNum, new LogisticFunction());
    }

    public String getHiddenLayer() {
        return hiddenLayer;
    }

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

    public int inLayerNeuronsNum() {
        return inLayerNeuronsNum;
    }

    public int outLayerNeuronsNum() {
        return outLayerNeuronsNum;
    }

    public int hiddenLayersNum() {
        return hiddenLayersNum;
    }

    public final void setInLayerNeuronsNum(int inLayerNeuronsNum) {
        checkValue(inLayerNeuronsNum);
        this.inLayerNeuronsNum = inLayerNeuronsNum;
    }

    public final void setOutLayerNeuronsNum(int outLayerNeuronsNum) {
        checkValue(outLayerNeuronsNum);
        this.outLayerNeuronsNum = outLayerNeuronsNum;
    }

    public int getLinksNum() {
        return linksNum;
    }

    public int layersNum() {
        return hiddenLayersNum() + 2;
    }

    public void setMinError(double min_error) {
        this.minError = min_error;
    }

    public double getMinError() {
        return minError;
    }

    public void setMaxIterationsNum(int max_iterations_num) {
        this.maxIterationsNum = max_iterations_num;
    }

    public int getMaxIterationsNum() {
        return maxIterationsNum;
    }

    public ActivationFunction getActivationFunction() {
        return hiddenFunction;
    }

    public ActivationFunction getOutActivationFunction() {
        return outerFunction;
    }

    public final void setActivationFunction(ActivationFunction function) {
        if (function == null) {
            throw new IllegalArgumentException();
        }
        this.hiddenFunction = function;
    }

    public final void setOutActivationFunction(ActivationFunction function) {
        if (function == null) {
            throw new IllegalArgumentException();
        }
        this.outerFunction = function;
    }

    public void setLearningAlgorithm(LearningAlgorithm algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException();
        }
        if (algorithm.network != this) {
            throw new IllegalArgumentException();
        }
        this.algorithm = algorithm;
    }

    public LearningAlgorithm getLearningAlgorithm() {
        return algorithm;
    }

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
