/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.InstancesHandler;
import eca.model.ClassifierDescriptor;
import weka.classifiers.Classifier;

import java.util.ArrayList;

/**
 * Basic interface for automatic selection of optimal options
 * for classifiers based on experiment series.
 *
 * @param <T> classifier type
 * @author Roman Batygin
 */
public interface Experiment<T extends Classifier> extends InstancesHandler {

    /**
     * Begins experiment.
     *
     * @throws Exception
     */
    void beginExperiment() throws Exception;

    /**
     * Return experiment history.
     *
     * @return experiment history
     */
    ArrayList<ClassifierDescriptor> getHistory();

    /**
     * Clears experiment history.
     */
    void clearHistory();

    /**
     * Returns iterations number of experiment.
     *
     * @return iterations number of experiment
     */
    int getNumIterations();

    /**
     * Sets iterations number of experiment.
     *
     * @param n iterations number of experiment
     */
    void setNumIterations(int n);

    /**
     * Classifier algorithm.
     *
     * @return classifier algorithm
     */
    T getClassifier();

}
