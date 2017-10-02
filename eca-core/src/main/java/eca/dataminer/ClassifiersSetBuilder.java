/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.ensemble.ClassifiersSet;
import eca.metrics.KNearestNeighbours;
import eca.neural.NeuralNetwork;
import eca.regression.Logistic;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.trees.ID3;
import weka.core.Instances;

/**
 * Class for creation default individual classifiers set,
 *
 * @author Roman Batygin
 */
public class ClassifiersSetBuilder {

    /**
     * Creates <tt>ClassifiersSet</tt> object.
     *
     * @param data <tt>Instances</tt> object (training data)
     * @param maximumFractionDigits maximum fraction digits
     * @return <tt>ClassifiersSet</tt> object
     */
    public static ClassifiersSet createClassifiersSet(Instances data, int maximumFractionDigits) {
        ClassifiersSet set = new ClassifiersSet();
        set.addClassifier(new CART());
        set.addClassifier(new ID3());
        set.addClassifier(new C45());
        set.addClassifier(new CHAID());
        set.addClassifier(new Logistic());
        NeuralNetwork neuralNetwork = new NeuralNetwork(data);
        neuralNetwork.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        set.addClassifier(neuralNetwork);
        KNearestNeighbours kNearestNeighbours = new KNearestNeighbours();
        kNearestNeighbours.getDecimalFormat().setMaximumFractionDigits(maximumFractionDigits);
        set.addClassifier(kNearestNeighbours);
        return set;
    }
}
