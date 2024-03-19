package eca.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Eca - service api config.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EcaServiceConfig {

    /**
     * Eca - service enabled?
     */
    private Boolean enabled = false;

    /**
     * Rabbit connection options
     */
    private RabbitConnectionOptions rabbitConnectionOptions = new RabbitConnectionOptions();

    /**
     * Evaluation request queue.
     */
    private String evaluationRequestQueue;

    /**
     * Evaluation optimizer request queue.
     */
    private String evaluationOptimizerRequestQueue;

    /**
     * Experiment request queue.
     */
    private String experimentRequestQueue;

    /**
     * Data loader url
     */
    private String dataLoaderUrl;

    /**
     * Token url
     */
    private String tokenUrl;

    /**
     * Client id
     */
    private String clientId;

    /**
     * Client secret
     */
    private String clientSecret;
}
