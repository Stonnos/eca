package eca.ensemble;

import weka.classifiers.Classifier;

/**
 * Ensemble algorithms utility class.
 *
 * @author Roman Batygin
 */
public class EnsembleUtils {

    /**
     * Calculates classifier weight.
     *
     * @param error classifier error value
     * @return classifier weight
     */
    public static double getClassifierWeight(double error) {
        return 0.5 * Math.log((1.0 - error) / error);
    }

    /**
     * Returns the specified number of threads. Is threads number is not specified then return 1.
     *
     * @param classifier {@link ConcurrentClassifier} object
     * @return threads number
     */
    public static int getNumThreads(ConcurrentClassifier classifier) {
        return classifier.getNumThreads() != null ? classifier.getNumThreads() : 1;
    }

    /**
     * Checks if classifier is heterogeneous ensemble.
     *
     * @param classifier - classifier
     * @return {@code true} if classifier is heterogeneous ensemble
     */
    public static boolean isHeterogeneousEnsembleClassifier(Classifier classifier) {
        return classifier instanceof AbstractHeterogeneousClassifier || classifier instanceof StackingClassifier;
    }
}
