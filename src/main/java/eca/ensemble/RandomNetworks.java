package eca.ensemble;

import eca.generators.NumberGenerator;
import eca.neural.NeuralNetwork;
import eca.neural.NeuralNetworkUtil;
import eca.neural.functions.AbstractFunction;
import weka.core.Instances;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * @author Roman Batygin
 */
public class RandomNetworks extends IterativeEnsembleClassifier {

    private static final double MIN_COEFFICIENT_VALUE = 1.0;
    private static final double MAX_COEFFICIENT_VALUE = 5.0;

    private List<AbstractFunction> activationFunctionsList;

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new NetworkBuilder(data);
    }

    @Override
    protected void initialize() throws Exception {
        votes = new MajorityVoting(new Aggregator(this));
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
            neuralNetwork.network().setHiddenLayer(NeuralNetworkUtil.generateRandomHiddenLayer(filteredData));
            AbstractFunction randomActivationFunction
                    = activationFunctionsList.get(random.nextInt(activationFunctionsList.size()));
            AbstractFunction cloneFunction = randomActivationFunction.clone();

            double coefficientValue = NumberGenerator.random(MIN_COEFFICIENT_VALUE, MAX_COEFFICIENT_VALUE);
            cloneFunction.setCoefficient(coefficientValue);
            neuralNetwork.network().setActivationFunction(cloneFunction);

            Instances bootstrap = sampler.bootstrap(filteredData);
            neuralNetwork.buildClassifier(bootstrap);
            classifiers.add(neuralNetwork);

            return ++index;
        }

    }
}
