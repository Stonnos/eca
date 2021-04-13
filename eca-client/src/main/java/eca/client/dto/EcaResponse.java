package eca.client.dto;

import lombok.Data;

import java.util.List;

/**
 * Eca - service response basic model.
 *
 * @author Roman Batygin
 */
@Data
public class EcaResponse {

    /**
     * Request unique identifier
     */
    private String requestId;

    /**
     * Technical status
     */
    private TechnicalStatus status;


    /**
     * Errors list
     */
    private List<MessageError> errors;
}
