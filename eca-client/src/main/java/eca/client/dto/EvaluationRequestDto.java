package eca.client.dto;

import eca.client.dto.options.ClassifierOptions;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;

/**
 * Evaluation request model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDto {

    /**
     * Train data uuid
     */
    private String dataUuid;

    /**
     * Classifier options
     */
    private ClassifierOptions classifierOptions;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Folds number for k * V cross - validation method
     */
    private Integer numFolds;

    /**
     * Tests number for k * V cross - validation method
     */
    private Integer numTests;

    /**
     * Seed value for k * V cross - validation method
     */
    private Integer seed;
}
