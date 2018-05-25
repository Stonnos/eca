package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.util.Random;

/**
 * Class for individual classifier building at each iteration of heterogeneous ensemble algorithm.
 *
 * @author Roman Batygin
 */
public class ClassifierBuilder {

    /**
     * Returns classifier model at the specified position in this collection
     * built on given training set.
     *
     * @param classifiers - classifiers set
     * @param i           - index of the element
     * @param data        - <tt>Instances</tt> object
     * @param seed        - seed value for single classifier
     * @return classifier model at the specified position in this collection
     * built on given training set
     * @throws Exception
     */
    public static Classifier buildClassifier(ClassifiersSet classifiers, int i, Instances data, int seed)
            throws Exception {
        Classifier classifier = classifiers.getClassifierCopy(i);
        initializeAndBuildClassifier(classifier, data, seed);
        return classifier;
    }

    /**
     * Returns classifier model at random position in this collection
     * built on given training set.
     *
     * @param classifiers - classifiers set
     * @param data        - {@link Instances} object
     * @param random      - {@link Random} object
     * @param seed        - seed value for single classifier
     * @return classifier model at random position in this collection built on given training set
     * @throws Exception
     */
    public static Classifier buildRandomClassifier(ClassifiersSet classifiers, Instances data, Random random, int seed)
            throws Exception {
        Classifier classifier = classifiers.randomClassifier(random);
        initializeAndBuildClassifier(classifier, data, seed);
        return classifier;
    }

    /**
     * Returns classifier model in this collection minimizing
     * classification error on given training set.
     *
     * @param classifiers - classifiers set
     * @param data        - <tt>Instances</tt> object
     * @param seed        - seed value for single classifier
     * @return classifier model in this collection minimizing
     * classification error on given training set
     * @throws Exception
     */
    public static Classifier builtOptimalClassifier(ClassifiersSet classifiers, Instances data, int seed)
            throws Exception {
        Classifier model = null;
        double minError = Double.MAX_VALUE;
        for (int i = 0; i < classifiers.size(); i++) {
            Classifier c = classifiers.getClassifierCopy(i);
            initializeAndBuildClassifier(c, data, seed);
            double error = Evaluation.error(c, data);
            if (error < minError) {
                minError = error;
                model = c;
            }
        }
        return model;
    }

    private static void initializeAndBuildClassifier(Classifier classifier, Instances data, int seed) throws Exception {
        if (classifier instanceof Randomizable) {
            ((Randomizable) classifier).setSeed(seed);
        }
        classifier.buildClassifier(data);
    }
}
