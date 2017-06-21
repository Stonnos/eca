/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import eca.beans.ClassifierDescriptor;
import java.util.ArrayList;
import weka.core.Instances;
import weka.classifiers.Classifier;

/**
 * 
 * @author Roman93
 * @param <T> 
 */
public interface Experiment<T extends Classifier> {

    void beginExperiment() throws Exception;

    ArrayList<ClassifierDescriptor> getHistory();

    void clearHistory();

    int getNumIterations();

    void setNumIterations(int n);

    Instances data();
    
    T getClassifier();

}
