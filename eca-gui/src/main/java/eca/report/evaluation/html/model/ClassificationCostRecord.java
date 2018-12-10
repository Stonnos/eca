package eca.report.evaluation.html.model;

import lombok.Data;

/**
 * Classification cost record model.
 */
@Data
public class ClassificationCostRecord {

    /**
     * Class name
     */
    private String classValue;

    /**
     * TP rate value
     */
    private String tpRate;

    /**
     * FP rate value
     */
    private String fpRate;

    /**
     * TN rate value
     */
    private String tnRate;

    /**
     * FN rate value
     */
    private String fnRate;

    /**
     * Recall value
     */
    private String recall;

    /**
     * Precision value
     */
    private String precision;

    /**
     * F - measure value
     */
    private String fMeasure;

    /**
     * AUC value
     */
    private String aucValue;
}
