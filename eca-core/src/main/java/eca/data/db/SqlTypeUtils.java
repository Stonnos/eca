package eca.data.db;

import java.sql.Types;
import java.util.Arrays;

/**
 * Sql types utility class.
 *
 * @author Roman Batygin
 */
public class SqlTypeUtils {

    private SqlTypeUtils() {
    }

    /**
     * Numeric column sql type
     */
    public static final String NUMERIC_TYPE = "NUMERIC(18,9)";
    /**
     * Datetime column sql type
     */
    public static final String DATETIME_TYPE = "DATETIME";
    /**
     * Date column sql type
     */
    public static final String DATE_TYPE = "DATE";
    /**
     * Timestamp column sql type
     */
    public static final String TIMESTAMP_TYPE = "TIMESTAMP";

    /**
     * Available column types of numeric attribute
     **/
    private static final int[] NUMERIC_TYPES = {Types.DOUBLE, Types.FLOAT,
            Types.INTEGER, Types.SMALLINT,
            Types.DECIMAL, Types.NUMERIC,
            Types.REAL, Types.TINYINT,
            Types.BIGINT};

    /**
     * Available column types of nominal attribute
     **/
    private static final int[] NOMINAL_TYPES = {Types.CHAR, Types.VARCHAR,
            Types.NCHAR, Types.NVARCHAR, Types.BOOLEAN, Types.BIT,
            Types.LONGVARCHAR, Types.LONGNVARCHAR};

    /**
     * Available column types of date attribute
     **/
    private static final int[] DATES_TYPES = {Types.DATE, Types.TIME,
            Types.TIMESTAMP};

    /**
     * Checks if specified sql type is nominal.
     *
     * @param type - sql type
     * @return {@code true} if sql type is nominal
     */
    public static boolean isNominal(int type) {
        return hasType(NOMINAL_TYPES, type);
    }

    /**
     * Checks if specified sql type is date.
     *
     * @param type - sql type
     * @return {@code true} if sql type is date
     */
    public static boolean isDate(int type) {
        return hasType(DATES_TYPES, type);
    }

    /**
     * Checks if specified sql type is numeric.
     *
     * @param type - sql type
     * @return {@code true} if sql type is numeric
     */
    public static boolean isNumeric(int type) {
        return hasType(NUMERIC_TYPES, type);
    }

    private static boolean hasType(int[] types, int type) {
        return Arrays.stream(types).anyMatch(t -> t == type);
    }
}
