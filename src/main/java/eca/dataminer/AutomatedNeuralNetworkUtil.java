package eca.dataminer;

import eca.neural.functions.ActivationFunction;
import eca.neural.functions.ExponentialFunction;
import eca.neural.functions.LogisticFunction;
import eca.neural.functions.SineFunction;
import eca.neural.functions.TanhFunction;

import java.util.Arrays;
import java.util.List;

/**
 * Class for creation default activation functions set.
 * @author Roman Batygin
 */
public class AutomatedNeuralNetworkUtil {

    /**
     * Creates activation functions list.
     * @return activation functions list
     */
    public static List<ActivationFunction> createActivationFunctions() {
        return Arrays.asList(new LogisticFunction(),
                new TanhFunction(), new SineFunction(), new ExponentialFunction(),
                new LogisticFunction(2.0));
    }
}
