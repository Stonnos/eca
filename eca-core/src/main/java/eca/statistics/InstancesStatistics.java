package eca.statistics;

import weka.core.Instances;

/**
 * Instances statistics.
 *
 * @author Roman Batygin
 */
public class InstancesStatistics {

    /**
     * Checks data for missing values.
     *
     * @param data {@link Instances} object
     * @return <tt>true</tt> if specified data has missing
     */
    public static boolean hasMissing(Instances data) {
        for (int i = 0; i < data.numInstances(); i++) {
            for (int j = 0; j < data.numAttributes(); j++) {
                if (data.instance(i).isMissing(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the number of numeric attributes.
     *
     * @param data {@link Instances} object
     * @return the number of numeric attributes
     */
    public static int numNumericAttributes(Instances data) {
        int count = 0;
        for (int j = 0; j < data.numAttributes(); j++) {
            if (data.attribute(j).isNumeric()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of nominal attributes.
     *
     * @param data {@link Instances} object
     * @return the number of nominal attributes
     */
    public static int numNominalAttributes(Instances data) {
        int count = 0;
        for (int j = 0; j < data.numAttributes(); j++) {
            if (data.attribute(j).isNominal()) {
                count++;
            }
        }
        return count;
    }
}
