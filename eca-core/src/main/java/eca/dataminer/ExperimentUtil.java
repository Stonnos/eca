package eca.dataminer;

import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import eca.trees.J48;
import eca.util.MathUtils;
import weka.core.Instances;

/**
 * Data miner utils class.
 *
 * @author Roman Batygin
 */
public class ExperimentUtil {

    /**
     * Returns the number of individual models combination.
     * The number of combinations is <code>P = sum[t = 1..r]r!/(t!(r-t)!)</code>
     *
     * @param r the number of individual models combination
     * @return the number of individual models combination
     */
    public static int getNumClassifiersCombinations(int r) {
        int fact = MathUtils.fact(r);
        int p = 0;
        for (int t = 1; t <= r; t++) {
            p += fact / (MathUtils.fact(t) * MathUtils.fact(r - t));
        }
        return p;
    }

    /**
     * Creates <tt>ClassifiersSet</tt> object.
     *
     * @param data {@link Instances} object (training data)
     * @param maximumFractionDigits maximum fraction digits
     * @return {@link ClassifiersSet} object
     */
    public static ClassifiersSet builtClassifiersSet(Instances data, int maximumFractionDigits) {
        ClassifiersSet set = new ClassifiersSet();
        set.addClassifier(new CART());
        set.addClassifier(new ID3());
        set.addClassifier(new C45());
        set.addClassifier(new CHAID());
        set.addClassifier(new Logistic());
        set.addClassifier(new J48());
        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        set.addClassifier(neuralNetwork);
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        set.addClassifier(kNearestNeighbours);
        return set;
    }
}
