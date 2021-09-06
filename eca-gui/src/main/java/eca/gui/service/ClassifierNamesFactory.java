package eca.gui.service;

import eca.dictionary.ClassifiersNamesDictionary;
import eca.dictionary.EnsemblesNamesDictionary;
import eca.ensemble.AdaBoostClassifier;
import eca.ensemble.HeterogeneousClassifier;
import eca.ensemble.ModifiedHeterogeneousClassifier;
import eca.ensemble.RandomNetworks;
import eca.ensemble.StackingClassifier;
import eca.ensemble.forests.ExtraTreesClassifier;
import eca.ensemble.forests.RandomForests;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import eca.trees.J48;
import weka.classifiers.Classifier;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Title service.
 *
 * @author Roman Batygin
 */
public class ClassifierNamesFactory {

    private static final Map<Class<?>, String> CLASSIFIERS_TITLES_MAP = newHashMap();

    static {
        CLASSIFIERS_TITLES_MAP.put(CART.class, ClassifiersNamesDictionary.CART);
        CLASSIFIERS_TITLES_MAP.put(C45.class, ClassifiersNamesDictionary.C45);
        CLASSIFIERS_TITLES_MAP.put(ID3.class, ClassifiersNamesDictionary.ID3);
        CLASSIFIERS_TITLES_MAP.put(CHAID.class, ClassifiersNamesDictionary.CHAID);
        CLASSIFIERS_TITLES_MAP.put(NeuralNetwork.class, ClassifiersNamesDictionary.NEURAL_NETWORK);
        CLASSIFIERS_TITLES_MAP.put(KNearestNeighbours.class, ClassifiersNamesDictionary.KNN);
        CLASSIFIERS_TITLES_MAP.put(Logistic.class, ClassifiersNamesDictionary.LOGISTIC);
        CLASSIFIERS_TITLES_MAP.put(J48.class, ClassifiersNamesDictionary.J48);
        CLASSIFIERS_TITLES_MAP.put(AdaBoostClassifier.class, EnsemblesNamesDictionary.BOOSTING);
        CLASSIFIERS_TITLES_MAP.put(RandomNetworks.class, EnsemblesNamesDictionary.RANDOM_NETWORKS);
        CLASSIFIERS_TITLES_MAP.put(RandomForests.class, EnsemblesNamesDictionary.RANDOM_FORESTS);
        CLASSIFIERS_TITLES_MAP.put(ExtraTreesClassifier.class, EnsemblesNamesDictionary.EXTRA_TREES);
        CLASSIFIERS_TITLES_MAP.put(HeterogeneousClassifier.class, EnsemblesNamesDictionary.HETEROGENEOUS_ENSEMBLE);
        CLASSIFIERS_TITLES_MAP.put(ModifiedHeterogeneousClassifier.class,
                EnsemblesNamesDictionary.MODIFIED_HETEROGENEOUS_ENSEMBLE);
        CLASSIFIERS_TITLES_MAP.put(StackingClassifier.class, EnsemblesNamesDictionary.STACKING);
    }

    /**
     * Gets classifier name.
     *
     * @param classifier - classifier object
     * @return classifier name
     */
    public static String getClassifierName(Classifier classifier) {
        String classifierName = CLASSIFIERS_TITLES_MAP.get(classifier.getClass());
        if (classifierName == null) {
            throw new IllegalArgumentException(String.format("Can't get classifier [%s] name",
                    classifier.getClass().getSimpleName()));
        }
        return classifierName;
    }
}
