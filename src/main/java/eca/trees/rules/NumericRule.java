/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import java.text.DecimalFormat;
import weka.core.Attribute;
import weka.core.Instance;

/**
 * Class for generating model of numeric attribute split rule.
 * @author Рома
 */
public class NumericRule extends AbstractRule {

    /** Threshold value **/
    private double meanValue;

    /**
     * Creates <code>NumericRule</code> object.
     * @param attribute split attribute
     * @exception IllegalArgumentException if split attribute is not numeric
     */
    public NumericRule(Attribute attribute) {
        super(attribute);
    }

    /**
     * Creates <code>NumericRule</code> object.
     * @param attribute split attribute
     * @param meanValue threshold value
     * @exception IllegalArgumentException if split attribute is not numeric
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
     * @return threshold value
     */
    public final double getMeanValue() {
        return meanValue;
    }

    /**
     * Sets threshold value.
     * @param meanValue threshold value
     */
    public final void setMeanValue(double meanValue) {
        this.meanValue = meanValue;
    }
    
    @Override
    public String rule(int i) {
        return i == 0 ? attribute().name() + " <= " + meanValue
                :  attribute().name() + " > " + meanValue;
    }

    /**
     * Returns string representation of rule.
     * @param i child index
     * @param fmt <code>DecimalFormat</code> object
     * @return string representation of rule
     */
    public String rule(int i, DecimalFormat fmt) {
        String result = attribute().isDate() ? attribute().formatDate(meanValue) : fmt.format(meanValue);
        return i == 0 ? attribute().name() + " <= " + result
                :  attribute().name() + " > " + result;
    }
    
}
