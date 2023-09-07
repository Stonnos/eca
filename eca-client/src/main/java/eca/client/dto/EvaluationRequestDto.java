package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.dto.databind.ClassifierDeserializer;
import eca.client.dto.databind.ClassifierSerializer;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;

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
     * Classifier model
     */
    @JsonSerialize(using = ClassifierSerializer.class)
    @JsonDeserialize(using = ClassifierDeserializer.class)
    private AbstractClassifier classifier;

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
