package eca.client.dto;

import lombok.Data;

/**
 * Eca - service response basic model.
 * @author Roman Batygin
 */
@Data
public class EcaResponse {

    /**
     * Technical status
     */
    private TechnicalStatus status;

    /**
     * Error message
     */
    private String errorMessage;
}
