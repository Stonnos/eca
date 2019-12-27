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
     * Track name
     */
    private String name;

    /**
     * Description string
     */
    private String description;

    /**
     * Additional data map
     */
    private Map<String, String> additionalData;
}
