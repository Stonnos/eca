package eca.config;

import lombok.Data;

/**
 * Database config.
 *
 * @author Roman Batygin
 */
@Data
public class DatabaseConfig {

    private String driver;
    private String host;
    private Integer port;
    private String dataBaseName;
    private String login;
    private String password;
}
