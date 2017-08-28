package eca.neural;

import eca.generators.NumberGenerator;
import weka.core.Instances;
import weka.core.Utils;

/**
 * Neural network utility class.
 * @author Roman Batygin
 */
public class NeuralNetworkUtil {

    /**
     * Creates one hidden layer with random neurons number
     * in interval [a, b). Where: <p>
     * a - minimum neurons number in hidden layer <p>
     * b - maximum neurons number in hidden layer <p>
     *
     * @param data {@link Instances} object
     * @return the string representation of hidden layer structure
     */
    public String generateRandomHiddenLayer(Instances data) {
        int n = (int) NumberGenerator.random(getMinNumNeuronsInHiddenLayer(data), getMaxNumNeuronsInHiddenLayer(data));
        return String.valueOf(n);
    }

    /**
     * Returns the minimum number of links in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the minimum number of links in hidden layers
     */
    public final int getMinLinksNum(Instances data) {
        return (int) (data.numClasses() * data.numInstances() / (1 + Utils.log2(data.numInstances())));
    }

    /**
     * Returns the maximum number of links in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the maximum number of links in hidden layers
     */
    public final int getMaxLinksNum(Instances data) {
        return data.numClasses() * (1 + data.numInstances() / (data.numAttributes() - 1))
                * (data.numAttributes() + data.numClasses()) + data.numClasses();
    }

    /**
     * Returns the minimum number of neurons in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the minimum number of neurons in hidden layers
     */
    public final int getMinNumNeuronsInHiddenLayer(Instances data) {
        return getMinLinksNum(data) / (data.numAttributes() + data.numClasses() - 1);
    }

    /**
     * Returns the maximum number of neurons in hidden layers.
     *
     * @param data {@link Instances} object
     * @return the maximum number of neurons in hidden layers
     */
    public final int getMaxNumNeuronsInHiddenLayer(Instances data) {
        return getMaxLinksNum(data) / (data.numAttributes() + data.numClasses() - 1);
    }
}
