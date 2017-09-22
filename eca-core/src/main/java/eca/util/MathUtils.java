package eca.util;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class MathUtils {

    /**
     * Calculates <tt>n!</tt>.
     *
     * @param n input number
     * @return the factorial value of number n
     */
    public static int fact(int n) {
        return n == 0 ? 1 : n * fact(n - 1);
    }
}
