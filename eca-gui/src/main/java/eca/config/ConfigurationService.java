package eca.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.db.model.DataBaseType;
import eca.exception.ConfigException;
import eca.util.FileUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
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

    private static final String UI_TEXT_PROPERTIES_PATH = "ui-text-properties.json";
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
            if (ConfigStorageType.FILE.equals(applicationConfig.getConfigStorageType())) {
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
        if (ConfigStorageType.FILE.equals(applicationConfig.getConfigStorageType())) {
            File file = getEcaServiceConfigFile();
            OBJECT_MAPPER.writeValue(file, ecaServiceConfig);
            log.info("Eca service config has been saved to file [{}]", file.getAbsolutePath());
        }
    }

    /**
     * Gets database config for specified database type.
     *
     * @param dataBaseType - database type
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

    /**
     * Loads ui text properties
     */
    public void loadUiTextProperties() {
        Map<String, String> uiTextMap = loadConfig(UI_TEXT_PROPERTIES_PATH, new TypeReference<>() {
        });
        uiTextMap.forEach(UIManager::put);
    }

    private <T> T loadConfig(String fileName, Class<T> configType) {
        log.info("Loads config from file [{}]", fileName);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName)) {
            T config = OBJECT_MAPPER.readValue(inputStream, configType);
            log.info("Config has been loaded from file [{}]", fileName);
            return config;
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, fileName, ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    private <T> T loadConfig(File file, Class<T> configType) {
        try {
            log.info("Loads config from file [{}]", file.getAbsolutePath());
            T config = OBJECT_MAPPER.readValue(file, configType);
            log.info("Config has been loaded from file [{}]", file.getAbsolutePath());
            return config;
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, file.getAbsolutePath(), ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    private <T> T loadConfig(String fileName, TypeReference<T> tTypeReference) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(
                fileName)) {
            log.info("Loads config from file [{}]", fileName);
            T config =  OBJECT_MAPPER.readValue(inputStream, tTypeReference);
            log.info("Config has been loaded from file [{}]", fileName);
            return config;
        } catch (IOException ex) {
            log.error(String.format(ERROR_FORMAT, fileName, ex.getMessage()));
            throw new ConfigException(ex);
        }
    }

    @SneakyThrows
    private File getEcaServiceConfigFile() {
        File file = new File(FileUtils.getCurrentDir(), ECA_SERVICE_CONFIG_PATH);
        if (!file.isFile()) {
            log.warn("File [{}] not exists. Create new one", file.getAbsolutePath());
            boolean created = file.createNewFile();
            if (created) {
                log.info("New [{}] file has been created", file.getAbsolutePath());
                ecaServiceConfig = new EcaServiceConfig();
                saveEcaServiceConfig();
            } else {
                log.warn("Can't create file [{}]", file.getAbsolutePath());
            }
        }
        return file;
    }
}
