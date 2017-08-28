package eca.core;

/**
 * Utility class.
 * @author Roman Batygin
 */
public class Utils {

    /**
     * Calculates <tt>n!</tt>
     * @param n input number
     * @return factorial value of number n
     */
    public static int fact(int n) {
        return n == 0 ? 1 : n * fact(n - 1);
    }
}
