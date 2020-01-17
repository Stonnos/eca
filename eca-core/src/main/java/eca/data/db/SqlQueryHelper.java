package eca.data.db;

import lombok.Data;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SQL helper class.
 *
 * @author Roman Batygin
 */
@Data
public class SqlQueryHelper {

    private static final String DELIMITER = "_";
    private static final String STRING_VALUE_FORMAT = "'%s'";
    private static final int VARCHAR_LENGTH = 255;
    private static final String VARCHAR_TYPE_FORMAT = "VARCHAR(%d)";
    private static final String NULL_VALUE = "NULL";
    private static final String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE %s (%s)";
    private static final String COLUMN_FORMAT = "%s %s, ";
    private static final String LAST_COLUMN_FORMAT = "%s %s";
    private static final String INSERT_QUERY_FORMAT = "INSERT INTO %s VALUES(%s)";
    private static final String VALUE_DELIMITER_FORMAT = "%s, ";

    /**
     * Date column type
     */
    private String dateColumnType = SqlTypeUtils.DATETIME_TYPE;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats attribute name to database column format for create table query.
     *
     * @param attribute    - attribute
     * @param columnFormat - column format
     * @return column string
     */
    public String formatAttribute(Attribute attribute, String columnFormat) {
        String attributeName = normalizeName(attribute.name());
        if (attribute.isNominal()) {
            return String.format(columnFormat, attributeName, String.format(VARCHAR_TYPE_FORMAT, VARCHAR_LENGTH));
        } else if (attribute.isDate()) {
            return String.format(columnFormat, attributeName, dateColumnType);
        } else if (attribute.isNumeric()) {
            return String.format(columnFormat, attributeName, SqlTypeUtils.NUMERIC_TYPE);
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
    public String formatValue(Instance instance, Attribute attribute) {
        if (instance.isMissing(attribute)) {
            return NULL_VALUE;
        } else if (attribute.isNominal()) {
            String val = eca.util.Utils.removeQuotes(instance.stringValue(attribute));
            return String.format(STRING_VALUE_FORMAT, truncateStringValue(val));
        } else if (attribute.isDate()) {
            return String.format(STRING_VALUE_FORMAT,
                    simpleDateFormat.format(new Date((long) instance.value(attribute))));
        } else if (attribute.isNumeric()) {
            return String.valueOf(instance.value(attribute));
        } else {
            throw new IllegalArgumentException(
                    String.format("Unexpected attribute '%s' type!", attribute.name()));
        }
    }

    /**
     * Builds 'CREATE TABLE' sql query for specified instances.
     *
     * @param tableName - table name
     * @param instances - instances
     * @return 'CREATE TABLE' sql query string
     */
    public String buildCreateTableQuery(String tableName, Instances instances) {
        StringBuilder queryString = new StringBuilder();
        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            queryString.append(formatAttribute(instances.attribute(i), COLUMN_FORMAT));
        }
        queryString.append(formatAttribute(instances.attribute(instances.numAttributes() - 1), LAST_COLUMN_FORMAT));
        return String.format(CREATE_TABLE_QUERY_FORMAT, tableName, queryString);
    }

    /**
     * Builds 'INSERT QUERY' sql query for specified instance.
     *
     * @param tableName - table name
     * @param instances - instances
     * @param instance  - instance
     * @return 'INSERT QUERY' sql query string
     */
    public String buildInsertQuery(String tableName, Instances instances, Instance instance) {
        StringBuilder queryString = new StringBuilder();
        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            queryString.append(String.format(VALUE_DELIMITER_FORMAT, formatValue(instance, instances.attribute(i))));
        }
        queryString.append(formatValue(instance, instances.attribute(instances.numAttributes() - 1)));
        return String.format(INSERT_QUERY_FORMAT, tableName, queryString);
    }

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
}
