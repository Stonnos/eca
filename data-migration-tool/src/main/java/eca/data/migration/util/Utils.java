package eca.data.migration.util;

import weka.core.Attribute;
import weka.core.Instance;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String DELIMITER = "_";
    private static final String STRING_VALUE_FORMAT = "'%s'";
    private static final int VARCHAR_LENGTH = 255;

    private static final String VARCHAR_TYPE_FORMAT = "VARCHAR(%d)";
    private static final String NUMERIC_TYPE = "NUMERIC";
    private static final String TIMESTAMP_TYPE = "TIMESTAMP";
    private static final String NULL_VALUE = "NULL";

    /**
     * Normalizes name for data base. Normalization includes:
     * 1. Replaces all non words and non numeric symbols to '_' symbol.
     * 2. Casts result string to lower case.
     *
     * @param name - name string
     * @return normalized name
     */
    public static String normalizeName(String name) {
        StringBuilder resultString = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            resultString.append(Character.isLetterOrDigit(name.charAt(i)) ? name.charAt(i) : DELIMITER);
        }
        return resultString.toString().toLowerCase();
    }

    /**
     * Truncate string value if its length is greater than 255.
     *
     * @param value - string value
     * @return truncated string
     */
    public static String truncateStringValue(String value) {
        return value.length() > VARCHAR_LENGTH ? value.substring(0, VARCHAR_LENGTH) : value;
    }

    /**
     * Formats attribute name to database column format for create table query.
     *
     * @param attribute    - attribute
     * @param columnFormat - column format
     * @return column string
     */
    public static String formatAttribute(Attribute attribute, String columnFormat) {
        String attributeName = normalizeName(attribute.name());
        if (attribute.isNominal()) {
            return String.format(columnFormat, attributeName, String.format(VARCHAR_TYPE_FORMAT, VARCHAR_LENGTH));
        } else if (attribute.isDate()) {
            return String.format(columnFormat, attributeName, TIMESTAMP_TYPE);
        } else if (attribute.isNumeric()) {
            return String.format(columnFormat, attributeName, NUMERIC_TYPE);
        } else {
            throw new IllegalArgumentException(
                    String.format("Unexpected attribute '%s' type!", attribute.name()));
        }
    }

    /**
     * Formats attribute value of specified instance for sql insert query.
     *
     * @param instance  - training data instance
     * @param attribute - attribute
     * @return formatted value
     */
    public static String formatValue(Instance instance, Attribute attribute) {
        if (instance.isMissing(attribute)) {
            return NULL_VALUE;
        } else if (attribute.isNominal()) {
            String val = eca.util.Utils.removeQuotes(instance.stringValue(attribute));
            return String.format(STRING_VALUE_FORMAT, truncateStringValue(val));
        } else if (attribute.isDate()) {
            return String.format(STRING_VALUE_FORMAT,
                    SIMPLE_DATE_FORMAT.format(new Date((long) instance.value(attribute))));
        } else if (attribute.isNumeric()) {
            return String.valueOf(instance.value(attribute));
        } else {
            throw new IllegalArgumentException(
                    String.format("Unexpected attribute '%s' type!", attribute.name()));
        }
    }
}
