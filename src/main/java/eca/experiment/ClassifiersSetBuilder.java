/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import eca.trees.ID3;
import eca.trees.C45;
import eca.trees.CART;
import eca.trees.CHAID;
import eca.neural.NeuralNetwork;
import eca.metrics.KNearestNeighbours;
import eca.ensemble.ClassifiersSet;
import eca.regression.Logistic;
import weka.core.Instances;

/**
 *
 * @author Roman93
 */
public class ClassifiersSetBuilder {

    public static ClassifiersSet createClassifiersSet(Instances data) {
        ClassifiersSet set = new ClassifiersSet();
        set.addClassifier(new CART());
        set.addClassifier(new ID3());
        set.addClassifier(new C45());
        set.addClassifier(new CHAID());
        set.addClassifier(new Logistic());
        set.addClassifier(new NeuralNetwork(data));
        set.addClassifier(new KNearestNeighbours());
        return set;
    }
}
