package eca.neural;

import eca.generators.NumberGenerator;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;
import weka.core.Utils;

import java.util.Random;

/**
 * Neural network utility class.
 *
 * @author Roman Batygin
 */
public class NeuralNetworkUtil {

    private static final int MIN_NEURONS_NUM_IN_HIDDEN_LAYER = 1;
    private static final int MIN_HIDDEN_LAYERS_NUMBER = 1;
    private static final int MAX_HIDDEN_LAYERS_NUMBER = 2;
    private static final int MIN_SCORE_VALUE = 1;
    private static final int MAX_SCORE_VALUE = 2;

    /**
     * Creates hidden layer with random neurons number
     * in interval [a, b). Where: <p>
     * a - minimum neurons number in hidden layer <p>
     * b - maximum neurons number in hidden layer <p>
     *
     * @param data {@link Instances} object
     * @return the string representation of hidden layer structure
     */
    public static String generateRandomHiddenLayer(Instances data) {
        double neuronsCount = generateNeuronsNumberInHiddenLayer(data);
        if (neuronsCount < MIN_NEURONS_NUM_IN_HIDDEN_LAYER) {
            return String.valueOf(MIN_NEURONS_NUM_IN_HIDDEN_LAYER);
        }
        Random random = new Random();
        int hiddenLayersCount = random.nextInt(MAX_HIDDEN_LAYERS_NUMBER) + MIN_HIDDEN_LAYERS_NUMBER;
        if (hiddenLayersCount == 1 || neuronsCount <= hiddenLayersCount) {
            return String.valueOf((int) neuronsCount);
        }
        double[] scores = new double[hiddenLayersCount];
        double resultSum = 0.0;
        for (int i = 0; i < scores.length; i++) {
            scores[i] = random.nextInt(MAX_SCORE_VALUE) + MIN_SCORE_VALUE;
            resultSum += scores[i];
        }
        double normalizationValue = neuronsCount / resultSum;
        resultSum = 0.0;
        for (int i = 0; i < scores.length; i++) {
            scores[i] = Math.round(scores[i] * normalizationValue);
            resultSum += scores[i];
        }
        if (neuronsCount != resultSum) {
            double residue = neuronsCount - resultSum;
            int randomIndex = random.nextInt(scores.length);
            scores[randomIndex] += residue;
        }
        StringBuilder hiddenLayerStr = new StringBuilder();
        hiddenLayerStr.append((int) scores[0]);
        for (int i = 1; i < scores.length; i++) {
            hiddenLayerStr.append(",").append((int) scores[i]);
        }
        return hiddenLayerStr.toString().replace(",0", StringUtils.EMPTY);
    }

    /**
     * Generates the random neurons number in hidden layer
     * in interval [a, b). Where: <p>
     * a - minimum neurons number in hidden layer <p>
     * b - maximum neurons number in hidden layer <p>
     *
     * @param data {@link Instances} object
     * @return the random neurons number in hidden layer.
     */
    public static int generateNeuronsNumberInHiddenLayer(Instances data) {
        return (int) NumberGenerator.random(getMinNumNeuronsInHiddenLayer(data),
                getMaxNumNeuronsInHiddenLayer(data));
    }

    /**
     * Returns the minimum number of links in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the minimum number of links in hidden layers
     */
    public static int getMinLinksNum(Instances data) {
        return (int) (data.numClasses() * data.numInstances() / (1 + Utils.log2(data.numInstances())));
    }

    /**
     * Returns the maximum number of links in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the maximum number of links in hidden layers
     */
    public static int getMaxLinksNum(Instances data) {
        return data.numClasses() * (1 + data.numInstances() / (data.numAttributes() - 1))
                * (data.numAttributes() + data.numClasses()) + data.numClasses();
    }

    /**
     * Returns the minimum number of neurons in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the minimum number of neurons in hidden layers
     */
    public static int getMinNumNeuronsInHiddenLayer(Instances data) {
        int minNumNeurons = getMinLinksNum(data) / (data.numAttributes() + data.numClasses() - 1);
        return minNumNeurons < MIN_NEURONS_NUM_IN_HIDDEN_LAYER ? MIN_NEURONS_NUM_IN_HIDDEN_LAYER : minNumNeurons;
    }

    /**
     * Returns the maximum number of neurons in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the maximum number of neurons in hidden layers
     */
    public static int getMaxNumNeuronsInHiddenLayer(Instances data) {
        return getMaxLinksNum(data) / (data.numAttributes() + data.numClasses() - 1);
    }

    /**
     * Calculates multilayer perceptron error by formula: <p>
     * <code>E = 0.5 * sum[i = 1..n](y[i]-d[i])^2</code>
     *
     * @param actual   - actual values of output vector
     * @param expected - expected values of output vector
     * @return the error value
     */
    public static double error(double[] actual, double[] expected) {
        double error = 0.0;
        for (int i = 0; i < actual.length; i++) {
            error += Math.pow(actual[i] - expected[i], 2);
        }
        return 0.5 * error;
    }
}
