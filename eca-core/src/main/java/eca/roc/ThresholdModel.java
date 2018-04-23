package eca.roc;

import lombok.Data;

/**
 * Roc - curve threshold model.
 *
 * @author Roman Batygin
 */
@Data
public class ThresholdModel {

    private double specificity;
    private double sensitivity;
    private double thresholdValue;
}
