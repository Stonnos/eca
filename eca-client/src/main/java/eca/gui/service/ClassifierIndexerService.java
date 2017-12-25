/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.service;

import weka.classifiers.Classifier;

/**
 * @author Roman Batygin
 */
public class ClassifierIndexerService {

    public static String getIndex(Classifier classifier) {
        return String.format("%s_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

    public static String getExperimentIndex(Classifier classifier) {
        return String.format("%sExperiment_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

    public static String getResultsIndex(Classifier classifier) {
        return String.format("%sResults_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

}
