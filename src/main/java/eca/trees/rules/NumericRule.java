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
 *
 * @author Рома
 */
public class NumericRule extends AbstractRule {
    
    private double meanValue;
    
    public NumericRule(Attribute attribute) {
        super(attribute);
    }
    
    public NumericRule(Attribute attribute, double meanValue) {
         this(attribute);
         this.meanValue = meanValue;
    }
    
    @Override
    public int getChild(Instance obj) {
        return obj != null ? (obj.value(attribute()) <= meanValue ? 0 : 1) : -1;
    }
    
    public final double getMeanValue() {
        return meanValue;
    }
    
    public final void setMeanValue(double meanValue) {
        this.meanValue = meanValue;
    }
    
    @Override
    public String rule(int i) {
        return i == 0 ? attribute().name() + " <= " + meanValue
                :  attribute().name() + " > " + meanValue;
    }
    
    public String rule(int i, DecimalFormat fmt) {
        String result = attribute().isDate() ? attribute().formatDate(meanValue) : fmt.format(meanValue);
        return i == 0 ? attribute().name() + " <= " + result
                :  attribute().name() + " > " + result;
    }
    
}
