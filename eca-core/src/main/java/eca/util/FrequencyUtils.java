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
     * Calculates freq
     *
     * @param data
     * @param attribute
     * @param frequencyData
     * @return
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

    public static int calculateFirstFrequency(Instances data, Attribute attribute, FrequencyData frequencyData) {
        int frequency = 0;
        for (int i = 0; i < data.numInstances(); i++) {
            if (IntervalUtils.containsValue(frequencyData, data.instance(i).value(attribute))) {
                frequency++;
            }
        }
        return frequency;
    }
}
