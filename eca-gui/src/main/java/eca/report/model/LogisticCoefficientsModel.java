package eca.report.model;

import eca.regression.Logistic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.core.Instances;

/**
 * Logistic coefficients model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticCoefficientsModel {

    /**
     * Report title
     */
    private String title;

    /**
     * Training data meta info
     */
    private Instances meta;

    /**
     * Logistic model
     */
    private Logistic logistic;
}
