package eca.core;

/**
 * Assertion class.
 *
 * @author Roman Batygin
 */

public class Assert {

    /**
     * Checked specified value for negative.
     *
     * @param val value
     */
    public static void notNegative(double val) {
        if (val < 0) {
            throw new IllegalArgumentException(String.format("Negative value: %d", val));
        }
    }
}
