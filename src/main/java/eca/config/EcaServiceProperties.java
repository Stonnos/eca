package eca.config;

import eca.io.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Properties;
import java.util.Set;

/**
 * Eca - service properties config.
 * @author Roman Batygin
 */
@Slf4j
public class EcaServiceProperties {

    public static final String PROPERTIES_FILE = "eca-service.properties";
    public static final String ECA_SERVICE_ENABLED = "eca.service.enabled";
    public static final String ECA_SERVICE_URL = "eca.service.url";
    public static final String ECA_SERVICE_PARAMS_MODEL = "eca.service.params.model";
    public static final String ECA_SERVICE_PARAMS_EVALUATION_METHOD = "eca.service.params.evaluationMethod";
    public static final String ECA_SERVICE_PARAMS_NUM_FOLDS = "eca.service.params.numFolds";
    public static final String ECA_SERVICE_PARAMS_NUM_TESTS = "eca.service.params.numTests";
    public static final String ECA_SERVICE_EVALUATION_METHOD_TRAINING = "eca.service.evaluationMethod.training";
    public static final String ECA_SERVICE_EVALUATION_METHOD_CROSS_VALIDATION =
            "eca.service.evaluationMethod.crossValidation";

    private static Properties PROPERTIES = new Properties();

    private static EcaServiceProperties INSTANCE;

    static {
        try (InputStream stream = new FileInputStream(getPropertiesFile())) {
            PROPERTIES.load(stream);
        } catch (Exception ex) {
            log.error("Can't load eca-service properties:", ex);
        }
    }

    private EcaServiceProperties() {
    }

    /**
     * Returns <tt>EcaServiceProperties</tt> instance.
     * @return <tt>EcaServiceProperties</tt> instance.
     */
    public static EcaServiceProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EcaServiceProperties();
        }
        return INSTANCE;
    }

    /**
     * Puts the pair (key, value) to map.
     * @param key key value
     * @param value value
     */
    public void put(String key, String value) {
        PROPERTIES.put(key, value);
    }

    /**
     * Saves properties to file.
     * @throws Exception
     */
    public void save() throws Exception {
        try (FileOutputStream out = new FileOutputStream(getPropertiesFile());
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "Cp1251"))) {
            PROPERTIES.store(writer, null);
        }
    }

    /**
     * Returns properties size.
     * @return properties size
     */
    public int size() {
        return PROPERTIES.size();
    }

    /**
     * Returns properties names.
     * @return properties names
     */
    public Set<String> getNames() {
        return PROPERTIES.stringPropertyNames();
    }

    /**
     * Gets property by key.
     * @param key key value
     * @return property value
     */
    public String getValue(String key) {
        return PROPERTIES.getProperty(key);
    }

    /**
     * Return eca - service enabled.
     * @return <tt>true</tt> if eca - service is enabled
     */
    public boolean getEcaServiceEnabled() {
        return Boolean.valueOf(PROPERTIES.getProperty(ECA_SERVICE_ENABLED));
    }

    /**
     * Returns eca - service url.
     * @return eca - service url
     */
    public String getEcaServiceUrl() {
        return PROPERTIES.getProperty(ECA_SERVICE_URL);
    }

    /**
     * Returns model parameter string.
     * @return model parameter string
     */
    public String getEcaServiceParamsModel() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_MODEL);
    }

    /**
     * Returns evaluation method parameter string.
     * @return evaluation method parameter string
     */
    public String getEcaServiceParamsEvaluationMethod() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_EVALUATION_METHOD);
    }

    /**
     * Returns the number of folds parameter string.
     * @return the number of folds parameter string
     */
    public String getEcaServiceParamsNumFolds() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_NUM_FOLDS);
    }

    /**
     * Returns the number of tests parameter string.
     * @return the number of tests parameter string
     */
    public String getEcaServiceParamsNumTests() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_NUM_TESTS);
    }

    /**
     * Returns training data evaluation method parameter string.
     * @return training data evaluation method parameter string
     */
    public String getEcaServiceEvaluationMethodTraining() {
        return PROPERTIES.getProperty(ECA_SERVICE_EVALUATION_METHOD_TRAINING);
    }

    /**
     * Returns cross - validation evaluation method parameter string.
     * @return cross - validation evaluation method parameter string
     */
    public String getEcaServiceEvaluationMethodCrossValidation() {
        return PROPERTIES.getProperty(ECA_SERVICE_EVALUATION_METHOD_CROSS_VALIDATION);
    }

    private static File getPropertiesFile() {
        return new File(FileUtils.getCurrentDir(), PROPERTIES_FILE);
    }

}
