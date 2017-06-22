/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.neural;

import java.util.Arrays;
import eca.core.converters.MinMaxNormalizer;
import eca.ensemble.Iterable;
import eca.ensemble.IterativeBuilder;
import eca.neural.functions.ActivationFunction;
import java.util.NoSuchElementException;
import weka.classifiers.AbstractClassifier;
import eca.core.evaluation.Evaluation;
import eca.filter.MissingValuesFilter;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import java.util.Random;
import eca.core.InstancesHandler;

/**
 *
 * @author Рома
 */
public class NeuralNetwork extends AbstractClassifier implements Iterable, InstancesHandler {

    private Instances data;
    private final MultilayerPerceptron network;
    private MinMaxNormalizer normalizer;
    private final MissingValuesFilter filter = new MissingValuesFilter();

    public NeuralNetwork(Instances data) {
        this.data = data;
        network = new MultilayerPerceptron(data.numAttributes() - 1,
                data.numClasses());
        network.setHiddenLayer(getMinNumNeuronsInHiddenLayer() < 1 ? "1" : String.valueOf(getMinNumNeuronsInHiddenLayer()));
    }

    public NeuralNetwork(Instances data, ActivationFunction function) {
        this(data);
        network.setActivationFunction(function);
    }

    public void setRandomHiddenLayer(Random r) {
        int n = (int) (Math.abs(r.nextDouble()) * (getMaxNumNeuronsInHiddenLayer()
                - getMinNumNeuronsInHiddenLayer()) + getMinNumNeuronsInHiddenLayer());
        network.setHiddenLayer(String.valueOf(n));
    }

    public final int getMinLinksNum() {
        return (int) (data.numClasses() * data.numInstances() / (1 + Utils.log2(data.numInstances())));
    }

    public final int getMaxLinksNum() {
        return  data.numClasses() * (1 + data.numInstances() / (data.numAttributes() - 1))
                * (data.numAttributes() + data.numClasses()) + data.numClasses();
    }

    public final int getMinNumNeuronsInHiddenLayer() {
        return getMinLinksNum() / (data.numAttributes() + data.numClasses() - 1);
    }

    public final int getMaxNumNeuronsInHiddenLayer() {
        return getMaxLinksNum() / (data.numAttributes() + data.numClasses() - 1);
    }

    @Override
    public String[] getOptions() {
        String[] options = {"Количество нейронов во входном слое:", String.valueOf(network().inLayerNeuronsNum()),
            "Количество нейронов в выходном слое:", String.valueOf(network().outLayerNeuronsNum()),
            "Количество скрытых слоев:", String.valueOf(network().hiddenLayersNum()),
            "Структура скрытого слоя:", network().getHiddenLayer(),
            "Максимальное число итераций:", String.valueOf(network().getMaxIterationsNum()),
            "Допустимая ошибка:", String.valueOf(network().getMinError()),
            "Активационная функция нейронов скрытого слоя:", network().getActivationFunction().getClass().getSimpleName(),
            "Активационная функция нейронов выходного слоя:", network().getOutActivationFunction().getClass().getSimpleName(),
            "Алгоритм обучения:", network().getLearningAlgorithm().getClass().getSimpleName()};
        if (network.getLearningAlgorithm() instanceof BackPropagation) {
            String[] algoritmOptions = network().getLearningAlgorithm().getOptions();
            options = Arrays.copyOf(options, options.length + algoritmOptions.length);
            for (int i = 0; i < algoritmOptions.length; i++) {
                options[options.length - algoritmOptions.length + i] = algoritmOptions[i];
            }
        }
        return options;
    }

    public MultilayerPerceptron network() {
        return network;
    }

    @Override
    public Instances getData() {
        return data;
    }

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

    private void initialize(Instances data) throws Exception {
        this.data = data;
        network.setInLayerNeuronsNum(data.numAttributes() - 1);
        network.setOutLayerNeuronsNum(data.numClasses());
        buildNetwork();
        normalizer = new MinMaxNormalizer(filter.filterInstances(data));
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
     *
     */
    private class NetworkBuilder extends IterativeBuilder {

        MultilayerPerceptron.IterativeBuilder trn;

        NetworkBuilder(Instances data) throws Exception {
            initialize(data);
            double[][] x = normalizer.normalizeInputValues();
            double[][] y = normalizer.normalizeOutputValues();
            trn = network().getIterativeBuilder(x, y);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index = trn.next();
            step = trn.step();
            return index;
        }

        @Override
        public int numIterations() {
            return network().getMaxIterationsNum();
        }

        @Override
        public Evaluation evaluation() throws Exception {
            if (!hasNext()) {
                Evaluation e = new Evaluation(data);
                e.evaluateModel(NeuralNetwork.this, data);
                return e;
            } else {
                return null;
            }
        }

        @Override
        public boolean hasNext() {
            return trn.isNext();
        }
    }

}
