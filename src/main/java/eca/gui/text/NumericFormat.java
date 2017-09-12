/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author Roman Batygin
 */
public class NumericFormat implements java.io.Serializable {

    public static DecimalFormat getInstance() {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(',');
        format.setDecimalFormatSymbols(custom);
        format.setGroupingUsed(false);
        return format;
    }

}
