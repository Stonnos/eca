package eca.statistics.contingency.model;

import lombok.Data;

/**
 * Chi squared test result model.
 *
 * @author Roman Batygin
 */
@Data
public class ChiSquareTestResult {

    /**
     * Chi squared calculated value
     */
    private double chiSquaredValue;
    /**
     * Ci squared critical value for specified significant level
     */
    private double chiSquaredCriticalValue;
    /**
     * Degrees number
     */
    private int df;
    /**
     * Significant level (probability value)
     */
    private double alpha;
    /**
     * Is there a statistical relationship between attributes?
     */
    private boolean significant;
}
