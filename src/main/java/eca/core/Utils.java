package eca.core;

/**
 * @author Roman Batygin
 */

public class Utils {

    public static int fact(int n) {
        return n == 0 ? 1 : n * fact(n - 1);
    }
}
