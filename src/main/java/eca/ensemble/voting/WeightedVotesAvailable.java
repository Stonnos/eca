/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.voting;

/**
 * Weighted votes method interface.
 *
 * @author Рома
 */
public interface WeightedVotesAvailable {

    /**
     * Returns <tt>true</tt> if weighted votes method is selected.
     *
     * @return <tt>true</tt> if weighted votes method is selected
     */
    boolean getUseWeightedVotesMethod();

    /**
     * Sets weighted votes method flag.
     *
     * @param flag weighted votes method flag
     */
    void setUseWeightedVotesMethod(boolean flag);
}
