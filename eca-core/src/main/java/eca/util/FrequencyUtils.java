package eca.util;

import eca.statistics.diagram.FrequencyData;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Frequency utility class.
 *
 * @author Roman Batygin
 */

public class FrequencyUtils {

    /**
     * Calculates frequency for given interval.
     *
     * @param data {@link Instances} object
     * @param attribute {@link Attribute} object
     * @param frequencyData {@link FrequencyData} object
     * @return the frequency value
     */
    public static int calculateFrequency(Instances data, Attribute attribute, FrequencyData frequencyData) {
        int frequency = 0;
        for (int i = 0; i < data.numInstances(); i++) {
            if (IntervalUtils.containsValueIncludeRightBound(frequencyData, data.instance(i).value(attribute))) {
                frequency++;
            }
        }
        return frequency;
    }

    /**
     * Calculates first frequency for given interval.
     *
     * @param data          {@link Instances} object
     * @param attribute     {@link Attribute} object
     * @param frequencyData {@link FrequencyData} object
     * @return the frequency value
     */
    public static int calculateFirstFrequency(Instances data, Attribute attribute, FrequencyData frequencyData) {
        int frequency = 0;
        for (int i = 0; i < data.numInstances(); i++) {
            if (IntervalUtils.containsValue(frequencyData, data.instance(i).value(attribute))) {
                frequency++;
            }
        }
        return frequency;
    }

    /**
     * Calculates the recommended intervals number by Stigess formula.
     *
     * @param n sample size
     * @return the value of recommended intervals number
     */
    public static int stigessFormula(int n) {
        return 1 + (int) weka.core.Utils.log2(n);
    }
}
