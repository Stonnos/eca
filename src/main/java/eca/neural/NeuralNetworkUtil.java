package eca.neural;

import eca.generators.NumberGenerator;
import eca.neural.functions.AbstractFunction;
import eca.neural.functions.ActivationFunction;
import org.reflections.Reflections;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Neural network utility class.
 * @author Roman Batygin
 */
public class NeuralNetworkUtil {

    private static final int MIN_HIDDEN_LAYERS_NUMBER = 1;
    private static final int MAX_HIDDEN_LAYERS_NUMBER = 3;

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
        int neuronsNumber = generateNeuronsNumberInHiddenLayer(data);

        Random random = new Random();

        int hiddenLayers = random.nextInt(MAX_HIDDEN_LAYERS_NUMBER) + MIN_HIDDEN_LAYERS_NUMBER;

        int layerSize = neuronsNumber / hiddenLayers;

        StringBuilder hiddenLayerStr = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < hiddenLayers; i++) {
            if (found) {
                hiddenLayerStr.append(",");
            }
            found = true;
            hiddenLayerStr.append(layerSize);
        }

        return hiddenLayerStr.toString();
    }

    /**
     * Generates the random neurons number in hidden layer
     * in interval [a, b). Where: <p>
     * a - minimum neurons number in hidden layer <p>
     * b - maximum neurons number in hidden layer <p>
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
        return getMinLinksNum(data) / (data.numAttributes() + data.numClasses() - 1);
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

    public static List<AbstractFunction> getActivationFunctions() throws Exception {
        Reflections reflections = new Reflections(AbstractFunction.class.getPackage().getName());

        Set<Class<? extends AbstractFunction>> classSet = reflections.getSubTypesOf(AbstractFunction.class);
        List<AbstractFunction> abstractFunctionList = new ArrayList<>();

        for (Class<? extends AbstractFunction> clazz : classSet) {
            AbstractFunction abstractFunction = clazz.getConstructor().newInstance();
            abstractFunctionList.add(abstractFunction);
        }

        return abstractFunctionList;
    }
}
