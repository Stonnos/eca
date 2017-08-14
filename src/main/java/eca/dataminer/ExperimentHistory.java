/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.model.ClassifierDescriptor;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Experiment unit model.
 *
 * @author Roman93
 */
public class ExperimentHistory implements java.io.Serializable {

    private ArrayList<ClassifierDescriptor> experiment;

    private Instances dataSet;

    /**
     * Creates <tt>ExperimentHistory</tt> object.
     *
     * @param experiment experiment history list
     * @param dataSet    training set object
     */
    public ExperimentHistory(ArrayList<ClassifierDescriptor> experiment, Instances dataSet) {
        this.experiment = experiment;
        this.dataSet = dataSet;
    }

    /**
     * Returns experiment history list.
     *
     * @return experiment history list
     */
    public ArrayList<ClassifierDescriptor> getExperiment() {
        return experiment;
    }

    /**
     * Sets experiment history list.
     *
     * @param experiment experiment history list
     */
    public void setExperiment(ArrayList<ClassifierDescriptor> experiment) {
        this.experiment = experiment;
    }

    /**
     * Returns training set object.
     *
     * @return training set object
     */
    public Instances getDataSet() {
        return dataSet;
    }

    /**
     * Sets training set object.
     *
     * @param dataSet training set object
     */
    public void setDataSet(Instances dataSet) {
        this.dataSet = dataSet;
    }

}
