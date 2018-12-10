package eca.report.evaluation.html.model;

import lombok.Data;

import java.util.List;

/**
 * Confusion matrix report model.
 *
 * @author Roman Batygin
 */
@Data
public class ConfusionMatrixReport {

    /**
     * Class values
     */
    private List<String> classValues;

    /**
     * Confusion matrix records
     */
    private List<ConfusionMatrixRecord> confusionMatrixRecords;
}
