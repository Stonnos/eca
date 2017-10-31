package eca.util;

import eca.statistics.diagram.IntervalData;

/**
 * Interval utility class.
 * @author Roman Batygin
 */

public class IntervalUtils {

    /**
     * Checks if the specified value is in interval (a, b].
     *
     * @param intervalData {@link IntervalData} object
     * @param val          value
     * @return <tt>true</tt> if the value is belong to interval (a, b]
     */
    public static boolean containsValueIncludeRightBound(IntervalData intervalData, double val) {
        return val > intervalData.getLowerBound() && val <= intervalData.getUpperBound();
    }

    /**
     * Checks if the specified value is in interval [a, b].
     *
     * @param intervalData {@link IntervalData} object
     * @param val          value
     * @return <tt>true</tt> if the value is belong to interval [a, b]
     */
    public static boolean containsValue(IntervalData intervalData, double val) {
        return val >= intervalData.getLowerBound() && val <= intervalData.getUpperBound();
    }
}
