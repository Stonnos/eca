package eca.data.migration.util;

/**
 * Utility class.
 *
 * @author Roman Batygin
 */
public class Utils {

    private static final String REPLACE_REGEX = "[^\\w]";
    private static final String DELIMITER = "_";

    public static final int VARCHAR_LENGTH = 255;

    /**
     * Normalizes name for data base. Normalization includes:
     * 1. Replaces all non words and non numeric symbols to '_' symbol.
     *
     * @param name - name string
     * @return normalized name
     */
    public static String normalizeName(String name) {
        return name.replaceAll(REPLACE_REGEX, DELIMITER);
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
}
