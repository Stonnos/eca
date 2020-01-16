package eca.gui.dictionary;

import lombok.experimental.UtilityClass;

/**
 * Eca common dictionary.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class CommonDictionary {


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
    public static final String RABBIT_HOST = "eca.service.rabbit.host";
    public static final String RABBIT_PORT = "eca.service.rabbit.port";
    public static final String RABBIT_USERNAME = "eca.service.rabbit.username";
    public static final String RABBIT_PASSWORD = "eca.service.rabbit.password";
}
