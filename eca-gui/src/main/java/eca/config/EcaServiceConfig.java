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
}
