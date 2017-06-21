/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import java.util.ArrayList;
import eca.beans.ClassifierDescriptor;
import weka.core.Instances;

/**
 *
 * @author Roman93
 */
public class ExperimentHistory implements java.io.Serializable {

    private ArrayList<ClassifierDescriptor> experiment;
    private Instances dataSet;

    public ExperimentHistory(ArrayList<ClassifierDescriptor> experiment, Instances dataSet) {
        this.experiment = experiment;
        this.dataSet = dataSet;
    }

    public ArrayList<ClassifierDescriptor> getExperiment() {
        return experiment;
    }

    public void setExperiment(ArrayList<ClassifierDescriptor> experiment) {
        this.experiment = experiment;
    }

    public Instances getDataSet() {
        return dataSet;
    }

    public void setDataSet(Instances dataSet) {
        this.dataSet = dataSet;
    }
    
}
