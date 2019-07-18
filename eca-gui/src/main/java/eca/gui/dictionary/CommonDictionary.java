package eca.gui.dictionary;

/**
 * Eca common dictionary.
 *
 * @author Roman Batygin
 */
public class CommonDictionary {

    private CommonDictionary() {
    }

    /**
     * Threads constraints
     */
    public static final int MAXIMUM_FRACTION_DIGITS = 7;
    public static final int MAXIMUM_INTEGER_DIGITS = 12;
    public static final int MIN_THREADS_NUM = 1;

    /**
     * K * V folds cross - validation method constraints
     */
    public static final int MINIMUM_NUMBER_OF_FOLDS = 2;
    public static final int MAXIMUM_NUMBER_OF_FOLDS = 100;
    public static final int MINIMUM_NUMBER_OF_TESTS = 1;
    public static final int MAXIMUM_NUMBER_OF_TESTS = 100;

    /**
     * Seed constraints
     */
    public static final int MIN_SEED = 1;
    public static final int MAX_SEED = 10000;

    /**
     * Tooltip dismiss time millis
     */
    public static final int TOOLTIP_DISMISS = 10000;

    /**
     * Eca - service options
     */
    public static final String ECA_SERVICE_ENABLED = "eca.service.enabled";
    public static final String ECA_API_URL = "eca.service.apiUrl";
    public static final String ECA_TOKEN_URL = "eca.service.tokenUrl";
    public static final String ECA_CLIENT_ID = "eca.service.clientId";
    public static final String ECA_CLIENT_SECRET = "eca.service.clientSecret";
    public static final String ECA_USERNAME = "eca.service.username";
    public static final String ECA_PASSWORD = "eca.service.password";
}
