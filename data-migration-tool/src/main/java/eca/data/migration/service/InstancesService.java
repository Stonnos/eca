package eca.data.migration.service;

import eca.data.migration.config.MigrationConfig;
import eca.data.migration.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import weka.core.Attribute;
import weka.core.Instances;

import javax.inject.Inject;
import java.util.Enumeration;

/**
 * Instances migration service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class InstancesService {

    private static final String CREATE_TABLE_QUERY_FORMAT = "CREATE TABLE %s (%s);";
    private static final String COLUMN_FORMAT = "%s %s, ";
    private static final String LAST_COLUMN_FORMAT = "%s %s";

    private final JdbcTemplate jdbcTemplate;
    private final TransactionalService transactionalMigrationService;
    private final MigrationConfig migrationConfig;

    /**
     * Constructor with spring dependency injection.
     *
     * @param jdbcTemplate                  - jdbc template bean
     * @param transactionalMigrationService - transactional migration service bean
     * @param migrationConfig               - migration config bean
     */
    @Inject
    public InstancesService(JdbcTemplate jdbcTemplate,
                            TransactionalService transactionalMigrationService,
                            MigrationConfig migrationConfig) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionalMigrationService = transactionalMigrationService;
        this.migrationConfig = migrationConfig;
    }

    /**
     * Saves training data into database.
     *
     * @param tableName - training data table name
     * @param instances - training data
     */
    @Transactional
    public void migrateInstances(String tableName, Instances instances) {
        log.info("Starting to migrate instances '{}' into table '{}'.", instances.relationName(), tableName);
        log.info("Starting to create table '{}'.", tableName);
        String createTableQuery = buildCreateTableQuery(tableName, instances);
        log.trace("create table query: {}", createTableQuery);
        jdbcTemplate.execute(createTableQuery);
        log.info("Table '{}' has been successfully created.", tableName);

        log.info("Starting to migrate data into table '{}'.", tableName);
        int batchSize = migrationConfig.getBatchSize();
        for (int offset = 0; offset < instances.numInstances(); offset += batchSize) {
            log.trace("Starting to migrate batch with limit = {}, offset = {} into table '{}'.", batchSize, offset,
                    tableName);
            transactionalMigrationService.migrateBatch(tableName, instances, batchSize, offset);
            log.trace("{} rows has been migrated into table '{}'.", offset + batchSize, tableName);
        }
        log.info("Data has been migrated into table '{}'.", tableName);
        log.info("Migration has been successfully completed. Instances '{}' has been migrated into table '{}.",
                instances.relationName(), tableName);
    }

    private String buildCreateTableQuery(String tableName, Instances instances) {
        StringBuilder queryString = new StringBuilder();
        for (Enumeration<Attribute> attributeEnumeration = instances.enumerateAttributes();
             attributeEnumeration.hasMoreElements(); ) {
            queryString.append(Utils.formatAttribute(attributeEnumeration.nextElement(), COLUMN_FORMAT));
        }
        queryString.append(Utils.formatAttribute(instances.classAttribute(), LAST_COLUMN_FORMAT));
        return String.format(CREATE_TABLE_QUERY_FORMAT, tableName, queryString);
    }
}
