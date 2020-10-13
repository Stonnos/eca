package eca;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

/**
 * Eca core test configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EcaCoreTestConfiguration {

    private static final String APPLICATION_TEST_PROPERTIES = "application-test.properties";
    private static final String FTP_PASSWORD = "ftp.password";
    private static final String FTP_USERNAME = "ftp.username";

    private static EcaCoreTestConfiguration instance;

    private static Properties properties = new Properties();

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
