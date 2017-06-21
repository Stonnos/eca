/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import eca.beans.ClassifierDescriptor;

/**
 *
 * @author Roman93
 */
public interface IterativeExperiment {

    boolean hasNext();

    ClassifierDescriptor next() throws Exception;

    int getPercent();
}
