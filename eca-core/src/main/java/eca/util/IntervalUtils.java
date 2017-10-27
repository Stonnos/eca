package eca.util;

import eca.statistics.diagram.IntervalData;

/**
 * @author Roman Batygin
 */

public class IntervalUtils {

    public static boolean containsValueIncludeRightBound(IntervalData intervalData, double val) {
        return val > intervalData.getLowerBound() && val <= intervalData.getUpperBound();
    }
}
