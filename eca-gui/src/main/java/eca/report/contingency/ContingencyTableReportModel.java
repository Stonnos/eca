package eca.report.contingency;

import eca.statistics.contingency.model.ChiSquareTestResult;
import lombok.Data;
import weka.core.Attribute;

import java.text.DecimalFormat;

/**
 * Contingency table report model.
 *
 * @author Roman Batygin
 */
@Data
public class ContingencyTableReportModel {

    /**
     * Row attribute
     */
    private Attribute rowAttribute;

    /**
     * Column attribute
     */
    private Attribute colAttribute;

    /**
     * Contingency matrix
     */
    private double[][] contingencyMatrix;

    /**
     * Chi squared test result
     */
    private ChiSquareTestResult chiSquareTestResult;

    /**
     * Decimal format
     */
    private DecimalFormat decimalFormat;
}
