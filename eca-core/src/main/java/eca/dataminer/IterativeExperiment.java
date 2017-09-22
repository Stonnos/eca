/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.evaluation.EvaluationResults;

/**
 * Interface for iterative experiment building.
 *
 * @author Roman Batygin
 */
public interface IterativeExperiment {

    /**
     * Returns <tt>true</tt> if the next iteration is exist.
     *
     * @return <tt>true</tt> if the next iteration is exist
     */
    boolean hasNext();

    /**
     * Returns the next constructed model.
     *
     * @return the next constructed model
     * @throws Exception
     */
    EvaluationResults next() throws Exception;

    /**
     * Returns the value of building percent.
     *
     * @return the value of building percent
     */
    int getPercent();
}
