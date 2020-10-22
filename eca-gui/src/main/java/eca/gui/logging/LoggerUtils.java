package eca.gui.logging;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

/**
 * Logger utils.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class LoggerUtils {

    /**
     * Logs error message.
     *
     * @param logger {@link Logger} object
     * @param ex     {@link Throwable} object
     */
    public static void error(Logger logger, Throwable ex) {
        logger.error("There was an error: {}", ex.getMessage(), ex);
    }
}
