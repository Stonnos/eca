package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.client.json.EvaluationResultsDeserializer;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EvaluationResponse extends EcaResponse {

    /**
     * Evaluation results
     */
    @JsonDeserialize(using = EvaluationResultsDeserializer.class)
    private EvaluationResults evaluationResults;

}
