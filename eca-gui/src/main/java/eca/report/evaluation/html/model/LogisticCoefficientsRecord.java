package eca.report.evaluation.html.model;

import lombok.Data;

import java.util.List;

/**
 * Logistic coefficients record.
 *
 * @author Roman Batygin
 */
@Data
public class LogisticCoefficientsRecord {

    /**
     * Attribute value
     */
    private String attrValue;

    /**
     * Coefficients list
     */
    private List<String> coefficients;
}
