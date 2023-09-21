package eca.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rabbit connection options.
 *
 * @author Roman Batygin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RabbitConnectionOptions {

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
