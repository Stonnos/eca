/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;

/**
 * Abstract class for iterative building of classifier model.
 * @author Рома
 */
public abstract class IterativeBuilder {
    
    protected int index;
    protected int step = 1;
    public abstract int next() throws Exception;
    public abstract boolean hasNext();
    public abstract int numIterations();

    /**
     * Returns <tt>Evaluation</tt> object if the model ia
     * already build, null otherwise.
     * @return <tt>Evaluation</tt> object
     * @throws Exception
     */
    public abstract Evaluation evaluation() throws Exception;

    /**
     * Returns the value of next iteration index.
     * @return the value of next iteration index
     */
    public int index() {
        return index;
    }

    /**
     * Returns the value of step between iterations.
     * @return the value of step between iterations
     */
    public int step() {
        return step;
    }

    /**
     * Returns the value of building percent.
     * @return the value of building percent
     */
    public int getPercent() {
        return index() * 100 / numIterations();
    }
    
}
