package eca.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Neural network report model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NeuralNetworkReport extends EvaluationReport {

    /**
     * Neural network image
     */
    private AttachmentImage networkImage;
}
