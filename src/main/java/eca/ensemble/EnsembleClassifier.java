/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.classifiers.Classifier;

import java.util.ArrayList;

/**
 * Basic interface for ensemble classification models.
 *
 * @author Roman Batygin
 */
public interface EnsembleClassifier {

    /**
     * Returns the structure of ensemble model as <tt>ArrayList</tt> object.
     *
     * @return the structure of ensemble model as <tt>ArrayList</tt> object.
     * @throws Exception
     */
    ArrayList<Classifier> getStructure() throws Exception;

}
