/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * @author Roman Batygin
 */
public class NumericFormat implements java.io.Serializable {

    private static final char DECIMAL_SEPARATOR = ',';

    public static DecimalFormat getInstance() {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(DECIMAL_SEPARATOR);
        format.setDecimalFormatSymbols(custom);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        return format;
    }

    public static final DecimalFormat getInstance(int maximumFractionDigits) {
        DecimalFormat format = getInstance();
        format.setMaximumFractionDigits(maximumFractionDigits);
        return format;
    }

}
