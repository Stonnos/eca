package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.core.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

/**
 * Evaluation request model.
 * @author Roman Batygin
 */
@Data
@JsonSerialize(using = EvaluationRequestSerializer.class)
public class EvaluationRequestDto {

    private AbstractClassifier classifier;

    private Instances data;

    private EvaluationMethod evaluationMethod;

    private Integer numFolds;

    private Integer numTests;
}
