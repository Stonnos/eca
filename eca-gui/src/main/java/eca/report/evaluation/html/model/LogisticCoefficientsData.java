package eca.report.evaluation.html.model;

import lombok.Data;

import java.util.List;

/**
 * Logistic coefficients data.
 *
 * @author Roman Batygin
 */
@Data
public class LogisticCoefficientsData {

    /**
     * Headers list
     */
    private List<String> headers;

    /**
     * Logistic coefficients data list
     */
    private List<LogisticCoefficientsRecord> coefficientsRecords;
}
