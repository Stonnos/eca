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
