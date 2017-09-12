/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core;

import weka.classifiers.Classifier;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Roman Batygin
 */
public class ClassifierIndexer {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("dd.MM.yy_HH.mm.ss");
    private final Date currentDate = new Date();

    public String getIndex(Classifier classifier) {
        return String.format("%s_%s", classifier.getClass().getSimpleName(), getCurrentDateToString());
    }

    public String getExperimentIndex(Classifier classifier) {
        return String.format("%sExperiment_%s",classifier.getClass().getSimpleName(), getCurrentDateToString());
    }

    public String getResultsIndex(Classifier classifier) {
        return String.format("%sResults_%s",classifier.getClass().getSimpleName(), getCurrentDateToString());
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public String getCurrentDateToString() {
        return FORMAT.format(currentDate);
    }

}
