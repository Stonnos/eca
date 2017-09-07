package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.ensemble.voting.WeightedVoting;
import eca.generators.NumberGenerator;
import eca.neural.MultilayerPerceptron;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.AbstractFunction;
import weka.core.Instances;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Class for generating Random networks model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set minimum error threshold for including classifier in ensemble <p>
 * <p>
 * Set maximum error threshold for including classifier in ensemble <p>
 * <p>
 * Set use bootstrap sample at each iteration. (Default: <tt>true</tt>) <p>
 *
 * @author Roman Batygin
 */
public class RandomNetworks extends ThresholdClassifier {

    private static final double MIN_COEFFICIENT_VALUE = 1.0;
    private static final double MAX_COEFFICIENT_VALUE = 5.0;

    /**
     * Available activation functions list
     */
    private List<AbstractFunction> activationFunctionsList;

    /**
     * USe bootstrap sample at each iteration?
     */
    private boolean useBootstrapSamples = true;

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new NetworkBuilder(data);
    }

    @Override
    public String[] getOptions() {
        return new String[] {
                "Число итераций:", String.valueOf(getIterationsNum()),
                "Минимальная допустимая ошибка сети:", String.valueOf(getMinError()),
                "Максимальная допустимая ошибка сети:", String.valueOf(getMaxError()),
                "Формирование обучающих выборок:", isUseBootstrapSamples() ?
                "Бутстрэп-выборки" : "Исходное обучающее множество"
        };
    }

    /**
     * Return the value of use bootstrap samples.
     * @return the value of use bootstrap samples
     */
    public boolean isUseBootstrapSamples() {
        return useBootstrapSamples;
    }

    /**
     * Sets the value of use bootstrap samples.
     * @param useBootstrapSamples the value of use bootstrap samples
     */
    public void setUseBootstrapSamples(boolean useBootstrapSamples) {
        this.useBootstrapSamples = useBootstrapSamples;
    }

    @Override
    protected void initialize() throws Exception {
        votes = new WeightedVoting(new Aggregator(this), getIterationsNum());
        activationFunctionsList = NeuralNetworkUtil.getActivationFunctions();
    }

    /**
     *
     */
    private class NetworkBuilder extends AbstractBuilder {

        Sampler sampler = new Sampler();
        Random random = new Random();

        public NetworkBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            NeuralNetwork neuralNetwork = new NeuralNetwork(filteredData);
            MultilayerPerceptron multilayerPerceptron = neuralNetwork.network();
            multilayerPerceptron.setHiddenLayer(NeuralNetworkUtil.generateRandomHiddenLayer(filteredData));
            AbstractFunction randomActivationFunction
                    = activationFunctionsList.get(random.nextInt(activationFunctionsList.size()));
            AbstractFunction cloneFunction = randomActivationFunction.clone();

            double coefficientValue = NumberGenerator.random(MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
            cloneFunction.setCoefficient(coefficientValue);
            multilayerPerceptron.setActivationFunction(cloneFunction);

            Instances sample;
            if (isUseBootstrapSamples()) {
                sample = sampler.bootstrap(filteredData);
            } else {
                sample = sampler.initial(filteredData);
            }

            neuralNetwork.buildClassifier(sample);

            double error = Evaluation.error(neuralNetwork, sample);

            if (error > getMinError() && error < getMaxError()) {
                classifiers.add(neuralNetwork);
                ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
            }

            if (index == getIterationsNum() - 1) {
                checkModel();
            }

            return ++index;
        }

    }
}
