package eca.statistics.diagram;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Frequency interval model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FrequencyData extends IntervalData {

    /**
     * Is numeric interval?
     */
    private boolean numeric;

    /**
     * Frequency value
     */
    private int frequency;

    /**
     * Default constructor
     */
    public FrequencyData() {
    }

    /**
     * Constructor with params.
     *
     * @param lowerBound - interval lower bound
     * @param upperBound - interval upper bound
     * @param numeric    - is numeric interval?
     */
    public FrequencyData(double lowerBound, double upperBound, boolean numeric) {
        super(lowerBound, upperBound);
        this.numeric = numeric;
    }
}
