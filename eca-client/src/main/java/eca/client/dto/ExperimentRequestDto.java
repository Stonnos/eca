package eca.client.dto;

import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

/**
 * Experiment request transport model.
 *
 * @author Roman Batygin
 */
@Data
public class ExperimentRequestDto {

    /**
     * Train data uuid
     */
    private String dataUuid;

    /**
     * Email
     */
    private String email;

    /**
     * Experiment type
     */
    private ExperimentType experimentType;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

}
