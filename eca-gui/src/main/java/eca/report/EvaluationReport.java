package eca.report;

import eca.core.evaluation.Evaluation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;
import java.util.Map;

/**
 * Evaluation report model.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationReport {

    /**
     * Statistics map
     */
    private Map<String, String> statisticsMap;

    /**
     * Initial training data
     */
    private Instances data;

    /**
     * Evaluation results
     */
    private Evaluation evaluation;

    /**
     * Classifier model
     */
    private Classifier classifier;

    /**
     * Attachment images
     */
    private List<AttachmentImage> attachmentImages;
}
