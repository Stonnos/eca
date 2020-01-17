/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.service;

import lombok.experimental.UtilityClass;
import weka.classifiers.Classifier;

/**
 * Implements classifier model indexing.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ClassifierIndexerService {

    /**
     * Returns the unique number for specified classifier.
     *
     * @param classifier {@link Classifier} object
     * @return the unique number for specified classifier
     */
    public static String getIndex(Classifier classifier) {
        return String.format("%s_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

    /**
     * Returns the unique number for specified classifiers experiment.
     *
     * @param classifier {@link Classifier} object
     * @return the unique number for specified classifiers experiment
     */
    public static String getExperimentIndex(Classifier classifier) {
        return String.format("%sExperiment_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

    /**
     * Returns the unique number for specified classification results.
     *
     * @param classifier {@link Classifier} object
     * @return the unique number for specified classification results.
     */
    public static String getResultsIndex(Classifier classifier) {
        return String.format("%sResults_%d", classifier.getClass().getSimpleName(), System.currentTimeMillis());
    }

}
