package eca.config;

import lombok.Data;

import java.util.List;

/**
 * Database test config.
 *
 * @author Roman Batygin
 */
@Data
public class DatabaseTestConfig {

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
    /**
     * Select queries test data list
     */
    private List<DbSelectQueryTestData> selectQueries;
    /**
     * Save data test data list
     */
    private List<String> saveDataFiles;
}
