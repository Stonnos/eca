package eca.util;

import org.apache.commons.lang3.StringUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */

public class Utils {

    private static final String MISSING_VALUE = "?";
    private static final String QUOTE = "'";
    private static final char POINT = '.';

    /**
     * Check value in list by specified predicate.
     *
     * @param list      list
     * @param val       value
     * @param predicate {@link BiPredicate} object
     * @return <tt>true</tt> if the value contains in list
     */
    public static <T> boolean contains(T[] list, T val, BiPredicate<T, T> predicate) {
        for (T x : list) {
            if (predicate.test(val, x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Normalizes arrays values.
     *
     * @param doubles - array of double values
     */
    public static void normalize(double[] doubles) {
        if (!weka.core.Utils.eq(weka.core.Utils.sum(doubles), 0.0)) {
            weka.core.Utils.normalize(doubles);
        }
    }

    /**
     * Gets all values of nominal attribute.
     *
     * @param attribute - attribute
     * @return values of nominal attribute
     */
    public static List<String> getAttributeValues(Attribute attribute) {
        if (attribute == null || !attribute.isNominal()) {
            return Collections.emptyList();
        } else {
            ArrayList<String> values = new ArrayList<>();
            for (int i = 0; i < attribute.numValues(); i++) {
                values.add(attribute.value(i));
            }
            return values;
        }
    }

    /**
     * Gets classifier input options map.
     *
     * @param classifier - classifier
     * @return input options map
     */
    public static Map<String, String> getClassifierInputOptionsMap(AbstractClassifier classifier) {
        if (classifier != null) {
            LinkedHashMap<String, String> optionsMap = new LinkedHashMap<>();
            String[] options = classifier.getOptions();
            for (int i = 0; i < options.length; i += 2) {
                optionsMap.put(options[i], options[i + 1]);
            }
            return optionsMap;
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Returns integer value if its not null, null otherwise
     *
     * @param value        - integer value
     * @param defaultValue - default integer value
     * @return integer value
     */
    public static int getIntValueOrDefault(Integer value, int defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Checks specified string value for missing value.
     *
     * @param val - string value
     * @return {@code true} if specified string value is missing value
     */
    public static boolean isMissing(String val) {
        return StringUtils.isEmpty(val) || val.equals(MISSING_VALUE);
    }

    /**
     * Trims and removes all quotes from string.
     *
     * @param val - specified string
     * @return result string
     */
    public static String removeQuotes(String val) {
        return !StringUtils.isEmpty(val) ? StringUtils.remove(val.trim(), QUOTE) : val;
    }

    /**
     * Puts value to specified map if it is not null.
     *
     * @param map   - map
     * @param key   - key value
     * @param value - value associated with key
     * @param <K>   key generic type
     * @param <V>   value generic type
     */
    public static <K, V> void putValueIfNotNull(Map<K, String> map, K key, V value) {
        if (value != null) {
            map.put(key, String.valueOf(value));
        }
    }

    /**
     * Gets instances name from file.
     *
     * @param fileName - file name
     * @return instances name
     */
    public static String getInstancesName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        int index = fileName.lastIndexOf(POINT);
        return index < 0 ? null : fileName.substring(0, index);
    }
}
