package eca.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Application- config service.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ApplicationConfigService {

    private static final String APPLICATION_CONFIG_PATH = "application-config.json";
    private static final String ECA_SERVICE_CONFIG = "eca-service-config.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static ApplicationConfigService applicationConfigService;
    private static ApplicationConfig applicationConfig;
    private static EcaServiceConfig ecaServiceConfig;

    private ApplicationConfigService() {
    }

    /**
     * Creates application config service singleton instance.
     *
     * @return application config service singleton instance
     */
    public static ApplicationConfigService getApplicationConfigService() {
        if (applicationConfigService == null) {
            applicationConfigService = new ApplicationConfigService();
        }
        return applicationConfigService;
    }

    /**
     * Loads application config.
     *
     * @return application config
     */
    public ApplicationConfig getApplicationConfig() {
        if (applicationConfig == null) {
            loadConfig(APPLICATION_CONFIG_PATH);
        }
        return applicationConfig;
    }

    /**
     * Loads eca - service config.
     *
     * @return eca - service config
     */
    public EcaServiceConfig getEcaServiceConfig() {
        if (ecaServiceConfig == null) {
            loadConfig(getEcaServiceConfigFile());
        }
        return ecaServiceConfig;
    }

    /**
     * Saves eca - service config into file.
     *
     * @throws IOException
     */
    public void saveEcaServiceConfig() throws IOException {
        OBJECT_MAPPER.writeValue(getEcaServiceConfigFile(), ecaServiceConfig);
    }

    private static void loadConfig(String fileName) {
        try (InputStream inputStream = ApplicationConfigService.class.getClassLoader().getResourceAsStream(
                fileName)) {
            applicationConfig = OBJECT_MAPPER.readValue(inputStream, ApplicationConfig.class);
        } catch (IOException ex) {
            log.error("There was an error while loading application config: {}", ex.getMessage());
        }
    }

    private static void loadConfig(File file) {
        try {
            ecaServiceConfig = OBJECT_MAPPER.readValue(file, EcaServiceConfig.class);
        } catch (IOException ex) {
            log.error("There was an error while loading application config: {}", ex.getMessage());
        }
    }

    private static File getEcaServiceConfigFile() {
        return new File(FileUtils.getCurrentDir(), ECA_SERVICE_CONFIG);
    }
}
