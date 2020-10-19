package eca.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.db.model.DataBaseType;
import eca.exception.ConfigException;
import eca.util.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration service.
 *
 * @author Roman Batygin
 */
@Slf4j
public class ConfigurationService {

    private static final String ERROR_FORMAT = "There was an error while loading config from '%s': %s";

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
    public static synchronized ConfigurationService getApplicationConfigService() {
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
            if (Boolean.TRUE.equals(getApplicationConfig().getProduction())) {
                ecaServiceConfig = loadConfig(getEcaServiceConfigFile(), EcaServiceConfig.class);
            } else {
                ecaServiceConfig = loadConfig(ECA_SERVICE_CONFIG_PATH, EcaServiceConfig.class);
            }
        }
        return ecaServiceConfig;
    }

    /**
     * Saves eca - service config into file.
     *
     * @throws IOException in case an I/O error
     */
    public void saveEcaServiceConfig() throws IOException {
        if (!Boolean.TRUE.equals(getApplicationConfig().getProduction())) {
            log.warn("Eca - service options saving is available only in production mode!");
        } else {
            OBJECT_MAPPER.writeValue(getEcaServiceConfigFile(), ecaServiceConfig);
        }
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

    /**
     * Gets specified icon path.
     *
     * @param iconType - icon type
     * @return icon path
     */
    public URL getIconUrl(IconType iconType) {
        Map<IconType, String> iconTypeStringMap = getApplicationConfig().getIcons();
        return getClass().getClassLoader().getResource(iconTypeStringMap.get(iconType));
    }

    private <T> T loadConfig(String fileName, Class<T> configType) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName)) {
            return OBJECT_MAPPER.readValue(inputStream, configType);
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, fileName, ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    private <T> T loadConfig(File file, Class<T> configType) {
        try {
            return OBJECT_MAPPER.readValue(file, configType);
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, file.getAbsolutePath(), ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    private <T> T loadConfig(String fileName, TypeReference<T> tTypeReference) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName)) {
            return OBJECT_MAPPER.readValue(inputStream, tTypeReference);
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, fileName, ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    private static File getEcaServiceConfigFile() {
        return new File(FileUtils.getCurrentDir(), ECA_SERVICE_CONFIG_PATH);
    }
}
