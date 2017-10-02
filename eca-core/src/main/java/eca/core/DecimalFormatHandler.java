package eca.core;

import java.text.DecimalFormat;

/**
 * Interface for obtaining decimal format.
 *
 * @author Roman Batygin
 */
public interface DecimalFormatHandler {

    /**
     * Returns decimal format.
     * @return {@link DecimalFormat} object
     */
    DecimalFormat getDecimalFormat();
}
