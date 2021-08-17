package eca.data.db;

import lombok.experimental.UtilityClass;

import java.sql.Types;
import java.util.Arrays;

/**
 * Sql types utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class SqlTypeUtils {

    /**
     * Numeric column sql type
     */
    public static final String NUMERIC_TYPE = "NUMERIC(22,9)";

    /**
     * Datetime column sql type
     */
    public static final String DATETIME_TYPE = "DATETIME";

    /**
     * Timestamp column sql type
     */
    public static final String TIMESTAMP_TYPE = "TIMESTAMP";

    /**
     * Real column type
     */
    public static final String REAL_COLUMN_TYPE = "REAL";

    /**
     * Text column type
     */
    public static final String TEXT_COLUMN_TYPE = "TEXT";

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
