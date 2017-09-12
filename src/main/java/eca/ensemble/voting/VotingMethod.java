/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.voting;

import eca.ensemble.Aggregator;
import weka.core.Instance;

/**
 * Abstract class that implements voting method.
 *
 * @author Roman Batygin
 */
public abstract class VotingMethod implements java.io.Serializable {

    /**
     * Aggregator object
     **/
    private Aggregator aggregator;

    /**
     * Creates <tt>VotingMethod</tt> object.
     *
     * @param aggregator <tt>Aggregator</tt>
     */
    protected VotingMethod(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * Returns <tt>Aggregator</tt> object.
     *
     * @return <tt>Aggregator</tt> object
     */
    public Aggregator aggregator() {
        return aggregator;
    }

    /**
     * Returns the class value of given instance.
     *
     * @param obj instance object
     * @return the class value of given instance
     * @throws Exception
     */
    public abstract double classifyInstance(Instance obj) throws Exception;

    /**
     * Returns the array of classes probabilities.
     *
     * @param obj instance object
     * @return the array of classes probabilities
     * @throws Exception
     */
    public abstract double[] distributionForInstance(Instance obj) throws Exception;

}
