package eca.statistics.diagram;

import lombok.Data;

/**
 * Interval model.
 *
 * @author Roman Batygin
 */
@Data
public class IntervalData {

    /**
     * Interval lower bound
     */
    private double lowerBound;

    /**
     * Interval upper bound
     */
    private double upperBound;

    /**
     * Default constructor
     */
    public IntervalData() {
    }

    /**
     * Constructor with params.
     *
     * @param lowerBound - interval lower bound
     * @param upperBound - interval upper bound
     */
    public IntervalData(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
}
