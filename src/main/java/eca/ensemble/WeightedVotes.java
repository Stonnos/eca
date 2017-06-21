/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instance;
import java.util.ArrayList;
/**
 *
 * @author Рома
 */
public class WeightedVotes extends VotesMethod {
       
    private ArrayList<Double> weights;
    
    public WeightedVotes(Aggregator aggregator, int size) {
        super(aggregator);
        weights = new ArrayList<>(size);
    }
    
    public void setWeight(double weight) {
        weights.add(weight);
    }
    
    public double getWeight(int i) {
        return weights.get(i);
    }
    
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
