/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;

import java.text.DecimalFormat;

/**
 * Class for generating model of numeric attribute split rule.
 *
 * @author Roman Batygin
 */
public class NumericRule extends AbstractRule {

    /**
     * Threshold value
     **/
    private double meanValue;

    /**
     * Creates <tt>NumericRule</tt> object.
     *
     * @param attribute split attribute
     * @throws IllegalArgumentException if the value of split attribute is null
     */
    public NumericRule(Attribute attribute) {
        super(attribute);
    }

    /**
     * Creates <tt>NumericRule</tt> object.
     *
     * @param attribute split attribute
     * @param meanValue threshold value
     * @throws IllegalArgumentException if split attribute is not numeric
     */
    public NumericRule(Attribute attribute, double meanValue) {
        this(attribute);
        this.meanValue = meanValue;
    }

    @Override
    public int getChild(Instance obj) {
        return obj != null ? (obj.value(attribute()) <= meanValue ? 0 : 1) : -1;
    }

    /**
     * Returns threshold value.
     *
     * @return threshold value
     */
    public final double getMeanValue() {
        return meanValue;
    }

    /**
     * Sets threshold value.
     *
     * @param meanValue threshold value
     */
    public final void setMeanValue(double meanValue) {
        this.meanValue = meanValue;
    }

    @Override
    public String rule(int i) {
        return i == 0 ? attribute().name() + " <= " + meanValue
                : attribute().name() + " > " + meanValue;
    }

    /**
     * Returns string representation of rule.
     *
     * @param i   child index
     * @param fmt <tt>DecimalFormat</tt> object
     * @return string representation of rule
     */
    public String rule(int i, DecimalFormat fmt) {
        String result = attribute().isDate() ? attribute().formatDate(meanValue) : fmt.format(meanValue);
        return i == 0 ? attribute().name() + " <= " + result
                : attribute().name() + " > " + result;
    }

}
