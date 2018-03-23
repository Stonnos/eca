package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.json.EvaluationRequestSerializer;
import eca.core.evaluation.EvaluationMethod;
import lombok.Data;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.util.Map;

/**
 * Evaluation request model.
 *
 * @author Roman Batygin
 */
@Data
@JsonSerialize(using = EvaluationRequestSerializer.class)
public class EvaluationRequestDto {

    /**
     * Classifier model
     */
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    private Instances data;

    /**
     * Evaluation method
     */
    private EvaluationMethod evaluationMethod;

    /**
     * Evaluation method options map
     */
    private Map<EvaluationOption, String> evaluationOptionsMap;
}
