package eca.statistics;

import org.springframework.util.Assert;
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

    private static final String NAN = "NaN";
    private Instances data;
    private DecimalFormat decimalFormat;

    /**
     * Creates <tt>AttributeStatistics</tt> object
     *
     * @param data          {@link Instances} object
     * @param decimalFormat {@link DecimalFormat} object
     */
    public AttributeStatistics(Instances data, DecimalFormat decimalFormat) {
        Assert.notNull(data, "Data is not specified!");
        Assert.notNull(decimalFormat, "Decimal format is not specified!");
        this.data = data;
        this.decimalFormat = decimalFormat;
    }

    /**
     * Returns sample object.
     *
     * @return {@link Instances} object
     */
    public Instances getData() {
        return data;
    }

    /**
     * Sets the sample object.
     *
     * @param data {@link Instances} object
     */
    public void setData(Instances data) {
        Assert.notNull(data, "Data is not specified!");
        this.data = data;
    }

    /**
     * Gets the maximum value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return attribute maximum value
     */
    public double getMax(Attribute a) {
        double maxVal = -Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) > maxVal) {
                maxVal = obj.value(a);
            }
        }
        return maxVal;
    }

    /**
     * Gets the maximum value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return the string representation of attribute maximum value
     */
    public String getMaxAsString(Attribute a) {
        double maxVal = getMax(a);
        return maxVal != -Double.MAX_VALUE ? decimalFormat.format(maxVal) : NAN;
    }

    /**
     * Gets the minimum value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return attribute minimum value
     */
    public double getMin(Attribute a) {
        double minVal = Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) < minVal) {
                minVal = obj.value(a);
            }
        }
        return minVal;
    }

    /**
     * Gets the minimum value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return the string representation of attribute minimum value
     */
    public String getMinAsString(Attribute a) {
        double minVal = getMin(a);
        return minVal != Double.MAX_VALUE ? decimalFormat.format(minVal) : NAN;
    }

    /**
     * Gets the mean value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return the string representation of attribute mean value
     */
    public String meanOrMode(Attribute a) {
        double meanOrMode = data.meanOrMode(a);
        return !Double.isNaN(meanOrMode) ? decimalFormat.format(meanOrMode) : NAN;
    }

    /**
     * Gets the variance value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return the string representation of attribute variance value
     */
    public String variance(Attribute a) {
        double var = data.variance(a);
        return !Double.isNaN(var) ? decimalFormat.format(var) : NAN;
    }

    /**
     * Gets the std. dev. value of given attribute.
     *
     * @param a {@link Attribute} object
     * @return the string representation of attribute std. dev. value
     */
    public String stdDev(Attribute a) {
        double stdDev = Math.sqrt(data.variance(a));
        return !Double.isNaN(stdDev) ? decimalFormat.format(stdDev) : NAN;
    }

    /**
     * Gets the number of values of given attribute.
     *
     * @param attribute {@link Attribute} object
     * @param val attribute value for comparing
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
