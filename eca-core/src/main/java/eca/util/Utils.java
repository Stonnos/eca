package eca.util;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.Map;

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
        for (T x : list) {
            if (predicate.test(val, x)) {
                return true;
            }
        }
        return false;
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
}
