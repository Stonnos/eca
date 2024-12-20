package eca.util;

import eca.statistics.diagram.IntervalData;
import lombok.experimental.UtilityClass;

/**
 * Interval utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class IntervalUtils {

    /**
     * Checks if the specified value is in interval (a, b].
     *
     * @param intervalData {@link IntervalData} object
     * @param val          value
     * @return <tt>true</tt> if the value is belong to interval (a, b]
     */
    public static boolean containsIncludeRightBound(IntervalData intervalData, double val) {
        return val > intervalData.getLowerBound() && val <= intervalData.getUpperBound();
    }

    /**
     * Checks if the specified value is in interval [a, b].
     *
     * @param intervalData {@link IntervalData} object
     * @param val          value
     * @return <tt>true</tt> if the value is belong to interval [a, b]
     */
    public static boolean contains(IntervalData intervalData, double val) {
        return val >= intervalData.getLowerBound() && val <= intervalData.getUpperBound();
    }
}
