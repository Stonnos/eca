package eca.util;

import eca.statistics.diagram.FrequencyData;
import lombok.experimental.UtilityClass;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Frequency utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class FrequencyUtils {

    /**
     * Calculates frequency for given interval.
     *
     * @param data          {@link Instances} object
     * @param attribute     {@link Attribute} object
     * @param frequencyData {@link FrequencyData} object
     */
    public static void calculateFrequency(Instances data, Attribute attribute, FrequencyData frequencyData) {
        int frequency = (int) data.stream().filter(instance -> !instance.isMissing(attribute) &&
                IntervalUtils.containsIncludeRightBound(frequencyData, instance.value(attribute))).count();
        frequencyData.setFrequency(frequency);
    }

    /**
     * Calculates first frequency for given interval.
     *
     * @param data          {@link Instances} object
     * @param attribute     {@link Attribute} object
     * @param frequencyData {@link FrequencyData} object
     */
    public static void calculateFirstFrequency(Instances data, Attribute attribute, FrequencyData frequencyData) {
        int frequency = (int) data.stream().filter(instance -> !instance.isMissing(attribute) &&
                IntervalUtils.contains(frequencyData, instance.value(attribute))).count();
        frequencyData.setFrequency(frequency);
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
