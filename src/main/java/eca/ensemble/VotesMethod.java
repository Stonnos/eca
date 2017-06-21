/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instance;

/**
 *
 * @author Рома
 */
public abstract class VotesMethod implements java.io.Serializable {
    
    private Aggregator aggregator;
    
    protected VotesMethod(Aggregator aggregator) {
        this.aggregator = aggregator;
    }
    
    public Aggregator aggregator() {
        return aggregator;
    }
    
    public abstract double classifyInstance(Instance obj) throws Exception;
    public abstract double[] distributionForInstance(Instance obj) throws Exception;

    public abstract String getDescription();
    
}
