package eca.statistics.diagram;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Frequency interval model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class FrequencyData extends IntervalData {

    private boolean numeric;

    private int frequency;

    @Builder
    public FrequencyData(double lowerBound, double upperBound, boolean numeric) {
        super(lowerBound, upperBound);
        this.numeric = numeric;
    }
}
