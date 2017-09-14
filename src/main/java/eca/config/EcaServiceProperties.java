package eca.config;

import eca.io.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Set;

/**
 * @author Roman Batygin
 */

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
        } catch (Exception e) {
        }
    }

    private EcaServiceProperties() {
    }

    public static EcaServiceProperties getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EcaServiceProperties();
        }
        return INSTANCE;
    }

    public void put(String key, String value) {
        PROPERTIES.put(key, value);
    }

    public void save() throws Exception {
        try (FileOutputStream out = new FileOutputStream(getPropertiesFile());
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "Cp1251"))) {
            PROPERTIES.store(writer, null);
        }
    }

    public int size() {
        return PROPERTIES.size();
    }

    public Set<String> getNames() {
        return PROPERTIES.stringPropertyNames();
    }

    public String getValue(String key) {
        return PROPERTIES.getProperty(key);
    }

    public boolean getEcaServiceEnabled() {
        return Boolean.valueOf(PROPERTIES.getProperty(ECA_SERVICE_ENABLED));
    }

    public String getEcaServiceUrl() {
        return PROPERTIES.getProperty(ECA_SERVICE_URL);
    }

    public String getEcaServiceParamsModel() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_MODEL);
    }

    public String getEcaServiceParamsEvaluationMethod() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_EVALUATION_METHOD);
    }

    public String getEcaServiceParamsNumFolds() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_NUM_FOLDS);
    }

    public String getEcaServiceParamsNumTests() {
        return PROPERTIES.getProperty(ECA_SERVICE_PARAMS_NUM_TESTS);
    }

    public String getEcaServiceEvaluationMethodTraining() {
        return PROPERTIES.getProperty(ECA_SERVICE_EVALUATION_METHOD_TRAINING);
    }

    public String getEcaServiceEvaluationMethodCrossValidation() {
        return PROPERTIES.getProperty(ECA_SERVICE_EVALUATION_METHOD_CROSS_VALIDATION);
    }

    private static File getPropertiesFile() {
        return new File(FileUtils.getCurrentDir(), PROPERTIES_FILE);
    }

}
