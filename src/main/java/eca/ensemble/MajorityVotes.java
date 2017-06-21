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
public class MajorityVotes extends VotesMethod {
       
    public MajorityVotes(Aggregator aggregator) {
        super(aggregator);
    }
    
    @Override
    public double classifyInstance(Instance obj) throws Exception {
        return aggregator().aggregate(obj);
    }
    
    @Override
    public double[] distributionForInstance(Instance obj) throws Exception {
        return aggregator().distributionForInstance(obj);
    }

    @Override
    public String getDescription() {
        return "Метод большинства голосов";
    }
    
}
