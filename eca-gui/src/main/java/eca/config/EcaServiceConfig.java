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
    private String evaluationRequestQueue = "evaluation-request-queue";

    /**
     * Evaluation optimizer request queue.
     */
    private String evaluationOptimizerRequestQueue = "evaluation-optimizer-request-queue";

    /**
     * Experiment request queue.
     */
    private String experimentRequestQueue = "experiment-request-queue";

    /**
     * Data loader url
     */
    private String dataLoaderUrl = "http://localhost:8080/eca-data-loader/api/external/upload-train-data";

    /**
     * Token url
     */
    private String tokenUrl = "http://localhost:8080/eca-oauth/oauth/token";

    /**
     * Client id
     */
    private String clientId = "external-api";

    /**
     * Client secret
     */
    private String clientSecret = "external_api_secret";
}
