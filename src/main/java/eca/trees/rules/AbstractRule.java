/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Abstract basic class for node spliting rule
 * @author Рома
 */
public abstract class AbstractRule implements java.io.Serializable {

    private Attribute attribute;

    protected AbstractRule(Attribute attribute) {
        if (attribute == null) {
            throw new NullPointerException();
        }
        this.attribute = attribute;
    }

    /**
     * @param obj instance
     * @return child node index
     */
    public abstract int getChild(Instance obj);

    /**
     *
     * @param i child index
     * @return
     */
    public abstract String rule(int i);

    /**
     *
     * @return spliting attribute
     */
    public final Attribute attribute() {
        return attribute;
    }
}
