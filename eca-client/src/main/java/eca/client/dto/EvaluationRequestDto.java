package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.dto.databind.ClassifierDeserializer;
import eca.client.dto.databind.ClassifierSerializer;
import eca.client.dto.databind.InstancesDeserializer;
import eca.client.dto.databind.InstancesSerializer;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Evaluation request model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationRequestDto {

    /**
     * Classifier model
     */
    @JsonSerialize(using = ClassifierSerializer.class)
    @JsonDeserialize(using = ClassifierDeserializer.class)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @JsonSerialize(using = InstancesSerializer.class)
    @JsonDeserialize(using = InstancesDeserializer.class)
    private Instances data;

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
