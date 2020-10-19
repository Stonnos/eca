package eca.config;

import com.fasterxml.jackson.core.type.TypeReference;
import eca.data.db.model.DataBaseType;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.google.common.collect.Maps.newHashMap;
import static eca.TestHelperUtils.loadConfig;

/**
 * Eca core test configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EcaCoreTestConfiguration {

    private static final String APPLICATION_TEST_PROPERTIES = "application-test.properties";
    private static final String DB_CONFIG_PATH = "db-test-config.json";

    private static final String FTP_PASSWORD = "ftp.password";
    private static final String FTP_USERNAME = "ftp.username";

    private static EcaCoreTestConfiguration instance;

    private static Properties properties = new Properties();

    private Map<DataBaseType, DatabaseTestConfig> databaseTestConfigMap;

    static {
        try {
            properties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(APPLICATION_TEST_PROPERTIES));
        } catch (IOException ex) {
            log.error("There wa an error while load configs: [{}]", ex.getMessage());
        }
    }

    private EcaCoreTestConfiguration() {
    }

    /**
     * Creates eca core test configuration singleton instance.
     *
     * @return eca core test configuration object
     */
    public static EcaCoreTestConfiguration getInstance() {
        if (instance == null) {
            instance = new EcaCoreTestConfiguration();
        }
        return instance;
    }

    /**
     * Gets database test config.
     *
     * @return database test config map
     */
    public Map<DataBaseType, DatabaseTestConfig> getDatabaseTestConfigMap() {
        if (databaseTestConfigMap == null) {
            databaseTestConfigMap =
                    loadConfig(DB_CONFIG_PATH, new TypeReference<HashMap<DataBaseType, DatabaseTestConfig>>() {
                    });
        }
        return newHashMap(databaseTestConfigMap);
    }

    /**
     * Gets ftp username.
     *
     * @return ftp username
     */
    public static String getFtpUsername() {
        return properties.getProperty(FTP_USERNAME);
    }

    /**
     * Gets ftp password.
     *
     * @return ftp password
     */
    public static String getFtpPassword() {
        return properties.getProperty(FTP_PASSWORD);
    }
}
