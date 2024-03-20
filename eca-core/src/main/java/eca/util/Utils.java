package eca.util;

import eca.core.evaluation.Evaluation;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class Utils {

    private static final String HEADER_FORMAT = ",%s";
    private static final String MISSING_VALUE = "?";
    private static final String QUOTE = "'";
    private static final char POINT = '.';
    private static final char COMMA_SEPARATOR = ',';

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
            if (predicate.test(x, val)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Shifts list elements to right from specified position.
     *
     * @param pos  - position
     * @param list - list
     * @param <T>  - list element generic type
     */
    public static <T> void shiftRight(int pos, List<T> list) {
        for (int i = list.size() - 1; i > pos; i--) {
            list.set(i, list.get(i - 1));
        }
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
     * Gets comma separated attributes as string.
     *
     * @param data - instances object
     * @return attributes as string
     */
    public static String getAttributesAsString(Instances data) {
        StringBuilder header = new StringBuilder(data.attribute(0).name());
        for (int i = 1; i < data.numAttributes(); i++) {
            header.append(String.format(HEADER_FORMAT, data.attribute(i).name()));
        }
        return header.toString();
    }

    /**
     * Gets all values of nominal attribute as array.
     *
     * @param attribute - attribute
     * @return values of nominal attribute
     */
    public static String[] getAttributeValuesAsArray(Attribute attribute) {
        if (attribute == null || !attribute.isNominal()) {
            return new String[0];
        } else {
            String[] values = new String[attribute.numValues()];
            for (int i = 0; i < attribute.numValues(); i++) {
                values[i] = attribute.value(i);
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

    /**
     * Gets attributes names as array.
     *
     * @param data - instances
     * @return attributes names as array
     */
    public static String[] getAttributeNames(Instances data) {
        String[] names = new String[data.numAttributes()];
        for (int i = 0; i < data.numAttributes(); i++) {
            names[i] = data.attribute(i).name();
        }
        return names;
    }

    /**
     * Splits string into words separated by commas.
     *
     * @param str - string to split
     * @return words array
     */
    public static String[] commaSeparatorSplit(String str) {
        return StringUtils.split(str, COMMA_SEPARATOR);
    }

    /**
     * Gets formatted evaluation value or missing value if it is NaN.
     *
     * @param evaluation   - evaluation object
     * @param function     - evaluation function
     * @param format       - decimal format
     * @param missingValue - missing value
     * @return formatted evaluation value
     */
    public static String getFormattedEvaluationValueOrMissing(Evaluation evaluation,
                                                              Function<Evaluation, Double> function,
                                                              DecimalFormat format,
                                                              String missingValue) {
        double value = function.apply(evaluation);
        return weka.core.Utils.isMissingValue(value) ? missingValue : format.format(value);
    }
}
