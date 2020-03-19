package eca.report.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Logistic regression report.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LogisticReport extends EvaluationReport {

    /**
     * Logistic coefficients
     */
    private LogisticCoefficientsModel logisticCoefficientsModel;
}
