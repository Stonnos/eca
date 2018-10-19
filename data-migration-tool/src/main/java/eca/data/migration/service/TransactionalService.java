package eca.data.migration.service;

import eca.data.db.SqlQueryHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Instances;

import javax.inject.Inject;

/**
 * Service for transactional data migration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class TransactionalService {

    private final JdbcTemplate jdbcTemplate;
    private final SqlQueryHelper sqlQueryHelper;

    /**
     * Constructor with spring dependency injection.
     *
     * @param jdbcTemplate   - jdbc template bean
     * @param sqlQueryHelper - sql query helper bean
     */
    @Inject
    public TransactionalService(JdbcTemplate jdbcTemplate, SqlQueryHelper sqlQueryHelper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlQueryHelper = sqlQueryHelper;
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
            jdbcTemplate.update(sqlQueryHelper.buildInsertQuery(tableName, instances, instances.instance(i)));
        }
    }
}
