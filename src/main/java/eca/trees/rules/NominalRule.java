/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Class for generating model of nominal attribute split rule.
 * @author Рома
 */
public class NominalRule extends AbstractRule {

    /**
     * Creates <code>NominalRule</code> object.
     * @param attribute split attribute
     * @exception IllegalArgumentException if split attribute is not nominal
     */
    public NominalRule(Attribute attribute) {
        super(attribute);
        if (!attribute.isNominal()) {
            throw new IllegalArgumentException("attribute " + attribute
                    + "must be nominal");
        }
    }

    @Override
    public int getChild(Instance obj) {
        return obj != null ? (int) obj.value(attribute()) : -1;
    }

    @Override
    public String rule(int i) {
        return attribute().isInRange(i) ? attribute().name() + " = " + i : null;
    }

}
