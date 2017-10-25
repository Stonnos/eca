/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees.rules;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * Class for generating model of nominal attribute binary split rule.
 *
 * @author Roman Batygin
 */
public class BinaryRule extends NominalRule {

    /**
     * Array of binary codes for nominal attribute values
     **/
    private int[] codes;

    /**
     * Creates <tt>BinaryRule</tt> object.
     *
     * @param attribute split attribute
     * @param codes    the array of binary codes for nominal attribute values
     */
    public BinaryRule(Attribute attribute, int[] codes) {
        super(attribute);
        this.setCodes(codes);
    }

    /**
     * Returns the array of binary codes for nominal attribute values.
     *
     * @return the array of binary codes for nominal attribute values
     */
    public final int[] getCodes() {
        return codes;
    }

    /**
     * Sets the array of binary codes for nominal attribute values.
     *
     * @param codes the array of binary codes for nominal attribute values
     */
    public final void setCodes(int[] codes) {
        this.codes = codes;
    }

    @Override
    public int getChild(Instance obj) {
        return codes[(int) obj.value(attribute())];
    }

    @Override
    public String rule(int i) {
        if (i < 0 || i > 1) {
            return null;
        }
        StringBuilder r = new StringBuilder(attribute().name() + " = ");
        boolean found = false;
        for (int j = 0; j < codes.length; j++) {
            if ((i == 0 && codes[j] == 0) || (i == 1 && codes[j] == 1)) {
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
