package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.client.json.EvaluationResponseDeserializer;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = EvaluationResponseDeserializer.class)
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    private EvaluationResults evaluationResults;

}
