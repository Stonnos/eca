/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.classifiers.Classifier;

import java.util.List;

/**
 * Basic interface for ensemble classification models.
 *
 * @author Roman Batygin
 */
public interface EnsembleClassifier {

    /**
     * Returns the structure of ensemble model.
     *
     * @return the structure of ensemble model as {@link List} object.
     * @throws Exception
     */
    List<Classifier> getStructure() throws Exception;

}
