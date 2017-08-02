package eca.statistics;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */

public class AttributeStatistics {

    private Instances data;
    private DecimalFormat decimalFormat;


    public AttributeStatistics(Instances data, DecimalFormat decimalFormat) {
        this.data = data;
        this.decimalFormat = decimalFormat;
    }

    public String getMax(Attribute a) {
        double maxVal = -Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) > maxVal) {
                maxVal = obj.value(a);
            }
        }
        return maxVal != -Double.MAX_VALUE ? decimalFormat.format(maxVal) : "NaN";
    }

    public String getMin(Attribute a) {
        double minVal = Double.MAX_VALUE;
        for (Instance obj : data) {
            if (!obj.isMissing(a) && obj.value(a) < minVal) {
                minVal = obj.value(a);
            }
        }
        return minVal != Double.MAX_VALUE ? decimalFormat.format(minVal) : "NaN";
    }

    public String meanOrMode(Attribute a) {
        double meanOrMode = data.meanOrMode(a);
        return !Double.isNaN(meanOrMode) ? decimalFormat.format(meanOrMode) : "NaN";
    }

    public String variance(Attribute a) {
        double var = data.variance(a);
        return !Double.isNaN(var) ? decimalFormat.format(var) : "NaN";
    }

    public String stdDev(Attribute a) {
        double stdDev = Math.sqrt(data.variance(a));
        return !Double.isNaN(stdDev) ? decimalFormat.format(stdDev) : "NaN";
    }

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
