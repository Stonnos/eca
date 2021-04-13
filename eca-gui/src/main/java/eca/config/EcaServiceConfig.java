package eca.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Eca - service api config.
 *
 * @author Roman Batygin
 */
@Data
@EqualsAndHashCode(exclude = {"evaluationRequestQueue", "evaluationOptimizerRequestQueue", "experimentRequestQueue"})
@NoArgsConstructor
@AllArgsConstructor
public class EcaServiceConfig {

    /**
     * Eca - service enabled?
     */
    private Boolean enabled = false;

    /**
     * Rabbit host
     */
    private String host = "localhost";

    /**
     * Rabbit port
     */
    private int port = 5672;

    /**
     * Rabbit username
     */
    private String username = "guest";

    /**
     * Rabbit password
     */
    private String password = "guest";

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
}
