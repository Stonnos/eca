package eca.config;

import lombok.Data;

/**
 * Eca - service api config.
 *
 * @author Roman Batygin
 */
@Data
public class EcaServiceConfig {

    /**
     * Eca - service enabled?
     */
    private Boolean enabled;
    /**
     * Evaluation url
     */
    private String evaluationUrl;
    /**
     * Experiment url
     */
    private String experimentUrl;
}
