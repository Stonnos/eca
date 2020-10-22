package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eca.client.dto.databind.EvaluationResultsDeserializer;
import eca.client.dto.databind.EvaluationResultsSerializer;
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
    @JsonSerialize(using = EvaluationResultsSerializer.class)
    @JsonDeserialize(using = EvaluationResultsDeserializer.class)
    private EvaluationResults evaluationResults;

}
