package eca.report;

import lombok.Data;

import java.util.List;

/**
 * Confusion matrix record model.
 *
 * @author Roman Batygin
 */
@Data
public class ConfusionMatrixRecord {

    /**
     * Class value
     */
    private String classValue;

    /**
     * Confusion matrix row for class value
     */
    private List<String> values;
}
