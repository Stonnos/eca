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
 *
 * @author Roman Batygin
 */
public class NominalRule extends AbstractRule {

    private static final String NOMINAL_RULE_FORMAT = "%s = %d";

    /**
     * Creates <tt>NominalRule</tt> object.
     *
     * @param attribute split attribute
     * @throws IllegalArgumentException if the value of split attribute is null
     */
    public NominalRule(Attribute attribute) {
        super(attribute);
    }

    @Override
    public int getChild(Instance obj) {
        return obj != null ? (int) obj.value(attribute()) : -1;
    }

    @Override
    public String rule(int i) {
        if (attribute().isInRange(i)) {
            return String.format(NOMINAL_RULE_FORMAT, attribute().name(), i);
        }
        return null;
    }

}
