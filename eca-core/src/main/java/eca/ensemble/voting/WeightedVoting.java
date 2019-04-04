/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.voting;

import eca.ensemble.Aggregator;
import weka.core.Instance;

import java.util.ArrayList;

/**
 * Implements weighted voting method.
 *
 * @author Roman Batygin
 */
public class WeightedVoting extends VotingMethod {

    /**
     * Creates <tt>MajorityVoting</tt> object.
     *
     * @param aggregator <tt>Aggregator</tt>
     */
    public WeightedVoting(Aggregator aggregator) {
        super(aggregator);
    }

    @Override
    public double classifyInstance(Instance obj) throws Exception {
        return aggregator().aggregate(obj, true);
    }

    @Override
    public double[] distributionForInstance(Instance obj) throws Exception {
        return aggregator().distributionForInstance(obj, true);
    }

}
