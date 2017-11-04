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
import eca.text.NumericFormat;
import weka.core.Instances;

import java.text.DecimalFormat;
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
    private DecimalFormat decimalFormat = NumericFormat.getInstance();

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new NetworkBuilder(data);
    }

    @Override
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public String[] getOptions() {
        return new String[] {
                EnsembleDictionary.NUM_ITS, String.valueOf(getIterationsNum()),
                EnsembleDictionary.NETWORK_MIN_ERROR, COMMON_DECIMAL_FORMAT.format(getMinError()),
                EnsembleDictionary.NETWORK_MAX_ERROR, COMMON_DECIMAL_FORMAT.format(getMaxError()),
                EnsembleDictionary.SAMPLING_METHOD,
                isUseBootstrapSamples() ? EnsembleDictionary.BOOTSTRAP_SAMPLE_METHOD
                        : EnsembleDictionary.TRAINING_SAMPLE_METHOD
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
    protected void initialize() throws Exception {
        votes = new WeightedVoting(new Aggregator(this), getIterationsNum());
    }

    /**
     * Random networks iterative builder.
     */
    private class NetworkBuilder extends AbstractBuilder {

        Sampler sampler = new Sampler();
        Random random = new Random();

        NetworkBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            NeuralNetwork neuralNetwork = new NeuralNetwork(filteredData);
            neuralNetwork.getDecimalFormat().setMaximumFractionDigits(getDecimalFormat().getMaximumFractionDigits());
            MultilayerPerceptron multilayerPerceptron = neuralNetwork.network();
            multilayerPerceptron.setHiddenLayer(NeuralNetworkUtil.generateRandomHiddenLayer(filteredData));
            ActivationFunctionType activationFunctionType =
                    ACTIVATION_FUNCTION_TYPES[random.nextInt(ACTIVATION_FUNCTION_TYPES.length)];
            AbstractFunction randomActivationFunction = activationFunctionType.handle(ACTIVATION_FUNCTION_BUILDER);
            double coefficientValue = NumberGenerator.random(MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
            randomActivationFunction.setCoefficient(coefficientValue);
            multilayerPerceptron.setActivationFunction(randomActivationFunction);

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
