package eca.util;

import java.util.function.BiPredicate;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */

public class Utils {

    /**
     * Check value in list by specified predicate.
     *
     * @param list      list
     * @param val       value
     * @param predicate {@link BiPredicate} object
     * @return <tt>true</tt> if the value contains in list
     */
    public static <T> boolean contains(T[] list, T val, BiPredicate<T, T> predicate) {
        for (int i = 0; i < list.length; i++) {
            if (predicate.test(val, list[i])) {
                return true;
            }
        }
        return false;
    }
}
