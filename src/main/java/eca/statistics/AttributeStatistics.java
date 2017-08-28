package eca.statistics;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.text.DecimalFormat;

/**
 * Attributes statistics calculation.
 *
 * @author Roman Batygin
 */
public class AttributeStatistics {

    public static final String NAN = "NaN";
    private Instances data;
    private DecimalFormat decimalFormat;

    /**
     * Creates <tt>AttributeStatistics</tt> object
     * @param data {@link Instances} object
     * @param decimalFormat {@link DecimalFormat} object
     */
    public AttributeStatistics(Instances data, DecimalFormat decimalFormat) {
        this.data = data;
        this.decimalFormat = decimalFormat;
    }

    /**
     * Gets the maximum value of given attribute.
     * @param a {@link Attribute} object
     * @return the string representation of attribute maximum value
     */
    public String getMax(Attribute a) {
        double maxVal = -Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) > maxVal) {
                maxVal = obj.value(a);
            }
        }
        return maxVal != -Double.MAX_VALUE ? decimalFormat.format(maxVal) : NAN;
    }

    /**
     * Gets the minimum value of given attribute.
     * @param a {@link Attribute} object
     * @return the string representation of attribute minimum value
     */
    public String getMin(Attribute a) {
        double minVal = Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) < minVal) {
                minVal = obj.value(a);
            }
        }
        return minVal != Double.MAX_VALUE ? decimalFormat.format(minVal) : NAN;
    }

    /**
     * Gets the mean value of given attribute.
     * @param a {@link Attribute} object
     * @return the string representation of attribute mean value
     */
    public String meanOrMode(Attribute a) {
        double meanOrMode = data.meanOrMode(a);
        return !Double.isNaN(meanOrMode) ? decimalFormat.format(meanOrMode) : NAN;
    }

    /**
     * Gets the variance value of given attribute.
     * @param a {@link Attribute} object
     * @return the string representation of attribute variance value
     */
    public String variance(Attribute a) {
        double var = data.variance(a);
        return !Double.isNaN(var) ? decimalFormat.format(var) : NAN;
    }

    /**
     * Gets the std. dev. value of given attribute.
     * @param a {@link Attribute} object
     * @return the string representation of attribute std. dev. value
     */
    public String stdDev(Attribute a) {
        double stdDev = Math.sqrt(data.variance(a));
        return !Double.isNaN(stdDev) ? decimalFormat.format(stdDev) : NAN;
    }

    /**
     * Gets the number of values of given attribute.
     * @param attribute {@link Attribute} object
     * @param attribute attribute value for comparing
     * @return the number of values of given attribute.
     */
    public int getValuesNum(Attribute attribute, double val) {
        int count = 0;
        for (Instance i : data) {
            if (i.value(attribute) == val) {
                count++;
            }
        }
        return count;
    }

}
