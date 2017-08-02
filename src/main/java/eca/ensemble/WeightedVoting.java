/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instance;

import java.util.ArrayList;

/**
 * Implements weighted voting method.
 * @author Рома
 */
public class WeightedVoting extends VotingMethod {

    /** Weights list **/
    private ArrayList<Double> weights;

    /**
     * Creates <tt>MajorityVoting</tt> object.
     * @param aggregator <tt>Aggregator</tt>
     */
    public WeightedVoting(Aggregator aggregator, int size) {
        super(aggregator);
        weights = new ArrayList<>(size);
    }

    /**
     * Sets the weight value.
     * @param weight the weight value
     */
    public void setWeight(double weight) {
        weights.add(weight);
    }

    /**
     * Return the weight value by index.
     * @param i index
     * @return the weight value
     */
    public double getWeight(int i) {
        return weights.get(i);
    }

    /**
     * Returns the weights list size.
     * @return the weights list size
     */
    public int size() {
        return weights.size();
    }
    
    @Override
    public double classifyInstance(Instance obj) throws Exception {
        return aggregator().aggregate(obj, weights);
    }
    
    @Override
    public double[] distributionForInstance(Instance obj) throws Exception {
        return aggregator().distributionForInstance(obj, weights);
    }

    @Override
    public String getDescription() {
        return "Метод взвешенного голосования";
    }
    
}
