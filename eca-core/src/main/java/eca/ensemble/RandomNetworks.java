package eca.ensemble;

import eca.core.DecimalFormatHandler;
import eca.core.evaluation.Evaluation;
import eca.ensemble.sampling.Sampler;
import eca.ensemble.voting.WeightedVoting;
import eca.generators.NumberGenerator;
import eca.neural.MultilayerPerceptron;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunctionBuilder;
import eca.neural.functions.ActivationFunctionType;
import eca.text.NumericFormatFactory;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.text.DecimalFormat;
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
public class RandomNetworks extends ThresholdClassifier implements DecimalFormatHandler {

    private static final double MIN_COEFFICIENT_VALUE = 1.0;
    private static final double MAX_COEFFICIENT_VALUE = 5.0;

    private static final ActivationFunctionBuilder ACTIVATION_FUNCTION_BUILDER = new ActivationFunctionBuilder();

    /**
     * Available activation functions list
     */
    private static final ActivationFunctionType[] ACTIVATION_FUNCTION_TYPES = ActivationFunctionType.values();

    /**
     * USe bootstrap sample at each iteration?
     */
    private boolean useBootstrapSamples = true;

    /**
     * Decimal format.
     */
    private DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    @Override
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public String[] getOptions() {
        return new String[] {
                EnsembleDictionary.NUM_ITS, String.valueOf(getNumIterations()),
                EnsembleDictionary.NETWORK_MIN_ERROR, COMMON_DECIMAL_FORMAT.format(getMinError()),
                EnsembleDictionary.NETWORK_MAX_ERROR, COMMON_DECIMAL_FORMAT.format(getMaxError()),
                EnsembleDictionary.SAMPLING_METHOD,
                isUseBootstrapSamples() ? EnsembleDictionary.BOOTSTRAP_SAMPLE_METHOD
                        : EnsembleDictionary.TRAINING_SAMPLE_METHOD,
                EnsembleDictionary.NUM_THREADS, String.valueOf(EnsembleUtils.getNumThreads(this)),
                EnsembleDictionary.SEED, String.valueOf(getSeed())
        };
    }

    /**
     * Return the value of use bootstrap samples.
     *
     * @return the value of use bootstrap samples
     */
    public boolean isUseBootstrapSamples() {
        return useBootstrapSamples;
    }

    /**
     * Sets the value of use bootstrap samples.
     *
     * @param useBootstrapSamples the value of use bootstrap samples
     */
    public void setUseBootstrapSamples(boolean useBootstrapSamples) {
        this.useBootstrapSamples = useBootstrapSamples;
    }

    @Override
    protected void initializeOptions() {
        votes = new WeightedVoting(new Aggregator(classifiers, filteredData), getNumIterations());
    }

    @Override
    protected Instances createSample(int iteration) throws Exception {
        return isUseBootstrapSamples() ? Sampler.bootstrap(filteredData, new Random(getSeed() + iteration)) :
                Sampler.initial(filteredData);
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) throws Exception {
        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
        neuralNetwork.setSeed(seeds[iteration]);
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(getDecimalFormat().getMaximumFractionDigits());
        MultilayerPerceptron multilayerPerceptron = neuralNetwork.getMultilayerPerceptron();
        Random networkRandom = new Random(seeds[iteration]);
        multilayerPerceptron.setHiddenLayer(NeuralNetworkUtil.generateRandomHiddenLayer(filteredData, networkRandom));
        ActivationFunctionType activationFunctionType =
                ACTIVATION_FUNCTION_TYPES[networkRandom.nextInt(ACTIVATION_FUNCTION_TYPES.length)];
        AbstractFunction randomActivationFunction = activationFunctionType.handle(ACTIVATION_FUNCTION_BUILDER);
        double coefficientValue = NumberGenerator.random(networkRandom, MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
        randomActivationFunction.setCoefficient(coefficientValue);
        multilayerPerceptron.setActivationFunction(randomActivationFunction);
        neuralNetwork.buildClassifier(data);
        return neuralNetwork;
    }

    @Override
    protected synchronized void addClassifier(Classifier classifier, Instances data) throws Exception {
        double error = Evaluation.error(classifier, data);
        if (error > getMinError() && error < getMaxError()) {
            classifiers.add(classifier);
            ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
        }
    }

}
