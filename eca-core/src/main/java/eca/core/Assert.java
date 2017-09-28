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

    /**
     * Check if the specified value is greater than zero.
     * @param val value
     * @param message error message
     */
    public static void greaterThanZero(double val, String message) {
        if (val <= 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
