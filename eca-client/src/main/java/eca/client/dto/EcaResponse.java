package eca.client.dto;

import lombok.Data;

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
     * Error message
     */
    private String errorMessage;
}
