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

    private AbstractClassifier classifier;

    private Instances data;

    private EvaluationMethod evaluationMethod;

    private Map<EvaluationOption, String> evaluationOptionsMap;
}
