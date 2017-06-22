/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import java.util.ArrayList;
import weka.classifiers.Classifier;

/**
 * Basic interface for ensemble classification models.
 * @author Рома
 */
public interface EnsembleClassifier {

    /**
     * Returns the structure of ensemble model as <code>ArrayList</code> object.
     * @return the structure of ensemble model as <code>ArrayList</code> object.
     * @throws Exception
     */
    ArrayList<Classifier> getStructure() throws Exception;

}
