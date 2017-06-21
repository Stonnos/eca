/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
/**
 *
 * @author Рома
 */
public abstract class IterativeBuilder {
    
    protected int index;
    protected int step = 1;
    public abstract int next() throws Exception;
    public abstract boolean hasNext();
    public abstract int numIterations();
    
    public abstract Evaluation evaluation() throws Exception;
    
    public int index() {
        return index;
    }
    
    public int step() {
        return step;
    }
    
    public int getPercent() {
        return index() * 100 / numIterations();
    }
    
}
