package eca.config;

import lombok.Data;

/**
 * Database config.
 *
 * @author Roman Batygin
 */
@Data
public class DatabaseConfig {

    /**
     * Jdbc driver class
     */
    private String driver;
    /**
     * Database host
     */
    private String host;
    /**
     * Database port
     */
    private Integer port;
    /**
     * Database name
     */
    private String dataBaseName;
    /**
     * Database user login
     */
    private String login;
    /**
     * Database user password
     */
    private String password;
}
