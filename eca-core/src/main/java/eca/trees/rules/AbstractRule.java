/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;

import java.util.Objects;

/**
 * Abstract class for generating model of node split rule.
 *
 * @author Roman Batygin
 */
public abstract class AbstractRule implements java.io.Serializable {

    private Attribute attribute;

    /**
     * Creates rule object.
     *
     * @param attribute split attribute
     * @throws IllegalArgumentException if the value of split attribute is null
     */
    protected AbstractRule(Attribute attribute) {
        Objects.requireNonNull(attribute, "Attribute is not specified!");
        this.attribute = attribute;
    }

    /**
     * Returns child node index.
     *
     * @param obj instance
     * @return child node index
     */
    public abstract int getChild(Instance obj);

    /**
     * Returns string representation of rule.
     *
     * @param i child index
     * @return string representation of rule
     */
    public abstract String rule(int i);

    /**
     * Returns split attribute.
     *
     * @return split attribute
     */
    public final Attribute attribute() {
        return attribute;
    }
}
