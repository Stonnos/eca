package eca.data.db;

import lombok.Data;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * SQL helper class.
 *
 * @author Roman Batygin
 */
@Data
public class SqlQueryHelper {

    private static final String DELIMITER = "_";
    private static final int VARCHAR_LENGTH = 255;
    private static final String VARCHAR_TYPE_FORMAT = "VARCHAR(%d)";
    private static final String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE %s (%s)";
    private static final String COLUMN_FORMAT = "%s %s, ";
    private static final String LAST_COLUMN_FORMAT = "%s %s";
    private static final String INSERT_QUERY_FORMAT = "INSERT INTO %s VALUES(%s)";
    private static final String VALUE_DELIMITER_FORMAT = "%s, ";
    private static final String PREPARED_QUERY_PARAMETER = "?";
    private static final String PRIMARY_KEY_COLUMN_FORMAT = "%s %s primary key, ";
    private static final String PRIMARY_KEY_TYPE = "int";

    /**
     * Date column type
     */
    private String dateColumnType = SqlTypeUtils.DATETIME_TYPE;

    /**
     * Numeric column type
     */
    private String numericColumnType = SqlTypeUtils.NUMERIC_TYPE;

    /**
     * Varchar column type
     */
    private String varcharColumnType = String.format(VARCHAR_TYPE_FORMAT, VARCHAR_LENGTH);

    /**
     * Using date as string?
     */
    private boolean useDateInStringFormat;

    /**
     * Use primary key column?
     */
    private boolean usePrimaryKeyColumn;

    /**
     * Primary key column name
     */
    private String primaryKeyColumnName;

    /**
     * Next ID value for primary key column
     */
    private int nextId;

    /**
     * Formats attribute name to database column format for create table query.
     *
     * @param attribute    - attribute
     * @param columnFormat - column format
     * @return column string
     */
    public String formatAttribute(Attribute attribute, String columnFormat) {
        String attributeName = formatName(attribute.name());
        if (attribute.isNominal()) {
            return String.format(columnFormat, attributeName, varcharColumnType);
        } else if (attribute.isDate()) {
            return String.format(columnFormat, attributeName, dateColumnType);
        } else if (attribute.isNumeric()) {
            return String.format(columnFormat, attributeName, numericColumnType);
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
    public Object getValue(Instance instance, Attribute attribute) {
        if (instance.isMissing(attribute)) {
            return null;
        } else if (attribute.isNominal()) {
            return formatNominalValue(instance.stringValue(attribute));
        } else if (attribute.isDate()) {
            return useDateInStringFormat ? instance.stringValue(attribute) :
                    Timestamp.valueOf(instance.stringValue(attribute));
        } else if (attribute.isNumeric()) {
            return instance.value(attribute);
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
        if (usePrimaryKeyColumn) {
            if (instances.attribute(primaryKeyColumnName) != null) {
                throw new IllegalStateException(
                        String.format("Primary key column [%s] matches with one of attribute", primaryKeyColumnName));
            }
            queryString.append(String.format(PRIMARY_KEY_COLUMN_FORMAT, primaryKeyColumnName, PRIMARY_KEY_TYPE));
        }
        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            queryString.append(formatAttribute(instances.attribute(i), COLUMN_FORMAT));
        }
        queryString.append(formatAttribute(instances.attribute(instances.numAttributes() - 1), LAST_COLUMN_FORMAT));
        return String.format(CREATE_TABLE_QUERY_FORMAT, tableName, queryString);
    }

    /**
     * Builds 'INSERT QUERY' sql query for specified instances.
     *
     * @param tableName - table name
     * @param instances - instances
     * @return 'INSERT QUERY' sql query string
     */
    public String buildPreparedInsertQuery(String tableName, Instances instances) {
        StringBuilder queryString = new StringBuilder();
        if (usePrimaryKeyColumn) {
            queryString.append(String.format(VALUE_DELIMITER_FORMAT, PREPARED_QUERY_PARAMETER));
        }
        for (int i = 0; i < instances.numAttributes() - 1; i++) {
            queryString.append(String.format(VALUE_DELIMITER_FORMAT, PREPARED_QUERY_PARAMETER));
        }
        queryString.append(PREPARED_QUERY_PARAMETER);
        return String.format(INSERT_QUERY_FORMAT, tableName, queryString);
    }

    /**
     * Prepares insert query parameters.
     *
     * @param instance - instance object
     * @return insert query parameters
     */
    public Object[] prepareQueryParameters(Instance instance) {
        List<Object> parameters = newArrayList();
        if (usePrimaryKeyColumn) {
            parameters.add(nextId);
        }
        IntStream.range(0, instance.numAttributes())
                .forEach(i -> {
                    Object value = getValue(instance, instance.attribute(i));
                    parameters.add(value);
                });
        return parameters.toArray();
    }

    /**
     * Performs name formatting. Formatting includes:
     * 1. Replaces all non words and non numeric symbols to '_' symbol.
     * 2. Casts result string to lower case.
     *
     * @param name - name string
     * @return formatted name
     */
    public static String formatName(String name) {
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
     * Formats nominal value.
     * Formatting includes:
     * 1. Quotes removal
     * 2. Value truncation to 255 length, if its length greater than 255 symbols
     *
     * @param value - value
     * @return formatted value
     */
    public static String formatNominalValue(String value) {
        String val = eca.util.Utils.removeQuotes(value);
        return truncateStringValue(val);
    }
}
