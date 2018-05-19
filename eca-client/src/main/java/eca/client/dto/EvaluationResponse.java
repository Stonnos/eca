package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.client.json.EvaluationResultsDeserializer;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    @JsonDeserialize(using = EvaluationResultsDeserializer.class)
    private EvaluationResults evaluationResults;

}
