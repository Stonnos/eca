package eca.util;

import eca.config.ApplicationProperties;

/**
 * Threads utility class.
 *
 * @author Roman Batygin
 */
public class ThreadsUtils {

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();

    public static final int MIN_NUM_THREADS = 1;
    public static final int MAX_NUM_THREADS = 10;

    /**
     * Returns the maximum number of threads used by parallel algorithms.
     *
     * @return the maximum number of threads used by parallel algorithms
     */
    public static int getMaxNumThreads() {
        Integer maxNumThreads = APPLICATION_PROPERTIES.getMaxThreads();
        return maxNumThreads != null ? maxNumThreads : MAX_NUM_THREADS;
    }
}
