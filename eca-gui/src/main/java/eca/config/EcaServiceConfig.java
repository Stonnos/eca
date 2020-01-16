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
    private Boolean enabled;

    /**
     * Rabbit host
     */
    private String host;

    /**
     * Rabbit port
     */
    private int port;

    /**
     * Rabbit username
     */
    private String username;

    /**
     * Rabbit password
     */
    private String password;
}
