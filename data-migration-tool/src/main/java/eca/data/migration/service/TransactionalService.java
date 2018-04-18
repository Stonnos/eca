package eca.data.migration.service;

import eca.data.migration.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Service for transactional data migration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class TransactionalService {

    private static final String INSERT_QUERY_FORMAT = "INSERT INTO %s VALUES(%s);";
    private static final String STRING_VALUE_FORMAT = "'%s'";
    private static final String VALUE_DELIMITER_FORMAT = "%s, ";

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor with spring dependency injection.
     *
     * @param jdbcTemplate - jdbc template bean
     */
    @Inject
    public TransactionalService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Saves training data batch into database.
     *
     * @param tableName - table name
     * @param instances - training data
     * @param limit     - batch size
     * @param offset    - offset value
     */
    @Transactional
    public void migrateBatch(String tableName, Instances instances, int limit, int offset) {
        for (int i = offset; i < Integer.min(instances.numInstances(), limit + offset); i++) {
            jdbcTemplate.update(buildInsertQuery(tableName, instances, instances.instance(i)));
        }
    }

    private String buildInsertQuery(String tableName, Instances instances, Instance instance) {
        StringBuilder queryString = new StringBuilder();
        for (Enumeration<Attribute> attributeEnumeration = instances.enumerateAttributes();
             attributeEnumeration.hasMoreElements(); ) {
            queryString.append(
                    String.format(VALUE_DELIMITER_FORMAT, formatValue(instance, attributeEnumeration.nextElement())));
        }
        queryString.append(formatValue(instance, instances.classAttribute()));
        return String.format(INSERT_QUERY_FORMAT, tableName, queryString);
    }

    private String formatValue(Instance instance, Attribute attribute) {
        if (attribute.isNominal()) {
            return String.format(STRING_VALUE_FORMAT, Utils.truncateStringValue(instance.stringValue(attribute)));
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
}
