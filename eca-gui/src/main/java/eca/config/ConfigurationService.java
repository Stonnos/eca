package eca.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.db.DataBaseType;
import eca.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ConfigurationService {

    private static final String APPLICATION_CONFIG_PATH = "application-config.json";
    private static final String ECA_SERVICE_CONFIG_PATH = "eca-service-config.json";
    private static final String DB_CONFIG_PATH = "db-config.json";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static ConfigurationService applicationConfigService;
    private ApplicationConfig applicationConfig;
    private EcaServiceConfig ecaServiceConfig;
    private Map<DataBaseType, DatabaseConfig> databaseConfigMap;

    private ConfigurationService() {
    }

    /**
     * Creates application config service singleton instance.
     *
     * @return application config service singleton instance
     */
    public static ConfigurationService getApplicationConfigService() {
        if (applicationConfigService == null) {
            applicationConfigService = new ConfigurationService();
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
            applicationConfig = loadConfig(APPLICATION_CONFIG_PATH, ApplicationConfig.class);
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
            ecaServiceConfig = loadConfig(getEcaServiceConfigFile(), EcaServiceConfig.class);
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

    /**
     * Gets database config for specified database type.
     *
     * @param dataBaseType - data base type
     * @return database config
     */
    public DatabaseConfig getDatabaseConfig(DataBaseType dataBaseType) {
        if (databaseConfigMap == null) {
            databaseConfigMap = loadConfig(DB_CONFIG_PATH, new TypeReference<HashMap<DataBaseType, DatabaseConfig>>() {
            });
        }
        return databaseConfigMap.get(dataBaseType);
    }

    private <T> T loadConfig(String fileName, Class<T> configType) {
        T config = null;
        try (InputStream inputStream = ConfigurationService.class.getClassLoader().getResourceAsStream(
                fileName)) {
            config = OBJECT_MAPPER.readValue(inputStream, configType);
        } catch (IOException ex) {
            log.error("There was an error while loading config from '{}': {}", fileName, ex.getMessage());
        }
        return config;
    }

    private <T> T loadConfig(File file, Class<T> configType) {
        T config = null;
        try {
            config = OBJECT_MAPPER.readValue(file, configType);
        } catch (IOException ex) {
            log.error("There was an error while loading config from '{}': {}", file.getAbsolutePath(), ex.getMessage());
        }
        return config;
    }

    private <T> T loadConfig(String fileName, TypeReference<T> tTypeReference) {
        T config = null;
        try (InputStream inputStream = ConfigurationService.class.getClassLoader().getResourceAsStream(
                fileName)) {
            config = OBJECT_MAPPER.readValue(inputStream, tTypeReference);
        } catch (IOException ex) {
            log.error("There was an error while loading config from '{}': {}", fileName, ex.getMessage());
        }
        return config;
    }

    private static File getEcaServiceConfigFile() {
        return new File(FileUtils.getCurrentDir(), ECA_SERVICE_CONFIG_PATH);
    }
}
