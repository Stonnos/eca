/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import java.util.Date;
import java.text.SimpleDateFormat;
import weka.classifiers.Classifier;

/**
 *
 * @author Roman93
 */
public class ClassifierIndexer {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yy_HH.mm.ss");
    private final Date currentDate = new Date();

    public String getIndex(Classifier classifier) {
        return classifier.getClass().getSimpleName() + "_" + getCurrentDateToString();
    }
    
    public String getExperimentIndex(Classifier classifier) {
        return classifier.getClass().getSimpleName() + "Experiment_" + getCurrentDateToString();
    }

    public String getResultsIndex(Classifier classifier) {
        return classifier.getClass().getSimpleName() + "Results_" + getCurrentDateToString();
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public String getCurrentDateToString() {
        return FORMAT.format(currentDate);
    }

}
