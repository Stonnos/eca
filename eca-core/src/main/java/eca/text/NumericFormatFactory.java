/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.text;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Numeric format factor class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class NumericFormatFactory implements java.io.Serializable {

    public static final char DECIMAL_SEPARATOR = ',';

    /**
     * Creates numeric format object.
     *
     * @return {@link DecimalFormat} object
     */
    public static DecimalFormat getInstance() {
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols custom = new DecimalFormatSymbols();
        custom.setDecimalSeparator(DECIMAL_SEPARATOR);
        format.setDecimalFormatSymbols(custom);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(Integer.MAX_VALUE);
        return format;
    }

    /**
     * Creates numeric format object with specified maximum fraction digits.
     *
     * @param maximumFractionDigits maximum fraction digits
     * @return {@link DecimalFormat} object
     */
    public static DecimalFormat getInstance(int maximumFractionDigits) {
        DecimalFormat format = getInstance();
        format.setMaximumFractionDigits(maximumFractionDigits);
        return format;
    }

}
