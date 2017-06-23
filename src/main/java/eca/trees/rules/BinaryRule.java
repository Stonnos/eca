/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;
import java.util.ArrayList;

/**
 * Class for generating model of nominal attribute binary split rule.
 * @author Рома
 */
public class BinaryRule extends NominalRule {

    /** Array of binary codes for nominal attribute values **/
    private int[] values;

    /**
     * Creates <tt>BinaryRule</tt> object.
     * @param attribute split attribute
     * @param values the array of binary codes for nominal attribute values
     */
    public BinaryRule(Attribute attribute, int[] values) {
        super(attribute);
        this.setValues(values);
    }

    /**
     * Returns the array of binary codes for nominal attribute values.
     * @return the array of binary codes for nominal attribute values
     */
    public final int[] values() {
        return values;
    }

    /**
     * Sets the array of binary codes for nominal attribute values.
     * @param values the array of binary codes for nominal attribute values
     */
    public final void setValues(int[] values) {
        this.values = values;
    }

    @Override
    public int getChild(Instance obj) {
        return values[(int) obj.value(attribute())];
    }

    @Override
    public String rule(int i) {
        if (i < 0 || i > 1) {
            return null;
        }
        StringBuilder r = new StringBuilder(attribute().name() + " = ");
        boolean found = false;
        for (int j = 0; j < values.length; j++) {
            if ((i == 0 && values[j] == 0) || (i == 1 && values[j] == 1)) {
                if (found) {
                    r.append(",");
                }
                found = true;
                r.append(j);
            }
        }
        return r.toString();
    }

}
