package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.json.ClassifierSerializer;
import eca.client.json.InstancesSerializer;
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
public class EvaluationRequestDto {

    /**
     * Classifier model
     */
    @JsonSerialize(using = ClassifierSerializer.class)
    private AbstractClassifier classifier;

    /**
     * Training data
     */
    @JsonSerialize(using = InstancesSerializer.class)
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
