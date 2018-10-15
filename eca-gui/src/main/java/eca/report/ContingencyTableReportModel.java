package eca.report;

import eca.statistics.contingency.ChiValueResult;
import lombok.Data;
import weka.core.Attribute;

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
    private ChiValueResult chiValueResult;
}
