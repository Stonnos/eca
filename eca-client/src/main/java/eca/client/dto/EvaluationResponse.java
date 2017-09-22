package eca.client.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eca.core.evaluation.EvaluationResults;
import lombok.Data;

/**
 * Evaluation response model.
 *
 * @author Roman Batygin
 */
@Data
@JsonDeserialize(using = EvaluationResponseDeserializer.class)
public class EvaluationResponse {

    private EvaluationResults evaluationResults;

    private TechnicalStatus status;

    private String errorMessage;
}
