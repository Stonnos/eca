package eca.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Eca - service track.
 *
 * @author Roman Batygin
 */
@Data
@Builder
public class EcaServiceTrack {

    /**
     * Message correlation id
     */
    private String correlationId;

    /**
     * Header info
     */
    private String header;

    /**
     * Additional data map
     */
    private Map<String, String> additionalData;
}
