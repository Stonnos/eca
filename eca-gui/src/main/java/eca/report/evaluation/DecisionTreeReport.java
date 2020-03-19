package eca.report.evaluation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Decision tree report model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DecisionTreeReport extends EvaluationReport {

    /**
     * Decision tree image
     */
    private AttachmentImage treeImage;
}
