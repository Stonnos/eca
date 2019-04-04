package eca.core;

/**
 * Assertion class.
 *
 * @author Roman Batygin
 */

public class Assert {

    private static final double ZERO = 0d;

    /**
     * Checked specified value for negative.
     *
     * @param val     - value
     * @param message - error message
     */
    public static void notNegative(double val, String message) {
        if (val < ZERO) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check if the specified value is greater than zero.
     *
     * @param val     - value
     * @param message - error message
     */
    public static void greaterThanZero(double val, String message) {
        if (val <= ZERO) {
            throw new IllegalArgumentException(message);
        }
    }
}
