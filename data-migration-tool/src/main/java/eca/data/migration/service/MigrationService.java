package eca.data.migration.service;

import eca.data.db.SqlQueryHelper;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.DataResource;
import eca.data.migration.config.MigrationConfig;
import eca.data.migration.exception.MigrationException;
import eca.data.migration.model.entity.MigrationLog;
import eca.data.migration.model.entity.MigrationLogSource;
import eca.data.migration.model.entity.MigrationStatus;
import eca.data.migration.repository.MigrationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Service for migration data file into database.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class MigrationService {

    private static final String TABLE_NAME_FORMAT = "%s_%d";

    private final MigrationConfig config;
    private final InstancesService instancesService;
    private final MigrationLogRepository migrationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param config                 - migration config bean
     * @param instancesService       - instances service bean
     * @param migrationLogRepository - migration log repository bean
     */
    @Inject
    public MigrationService(MigrationConfig config,
                            InstancesService instancesService,
                            MigrationLogRepository migrationLogRepository) {
        this.config = config;
        this.instancesService = instancesService;
        this.migrationLogRepository = migrationLogRepository;
    }

    /**
     * Migrates training data file into database.
     *
     * @param dataResource       - training data resource
     * @param migrationLogSource - migration log source
     */
    public void migrateData(DataResource dataResource, MigrationLogSource migrationLogSource) {
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setDateFormat(config.getDateFormat());
        dataLoader.setSource(dataResource);
        log.info("Starting to migrate file '{}'.", dataResource.getFile());
        MigrationLog migrationLog = createAndSaveMigrationLog(dataResource, migrationLogSource);
        try {
            Instances instances = dataLoader.loadInstances();
            log.info("Data has been loaded from file '{}'", dataResource.getFile());
            instancesService.migrateInstances(migrationLog.getTableName(), instances);
            migrationLog.setMigrationStatus(MigrationStatus.SUCCESS);
        } catch (Exception ex) {
            migrationLog.setMigrationStatus(MigrationStatus.ERROR);
            migrationLog.setDetails(ex.getMessage());
            throw new MigrationException(ex.getMessage());
        } finally {
            migrationLog.setFinishDate(LocalDateTime.now());
            migrationLogRepository.save(migrationLog);
        }
    }

    private synchronized MigrationLog createAndSaveMigrationLog(DataResource dataResource,
                                                                MigrationLogSource migrationLogSource) {
        long lastTableIndex = migrationLogRepository.findLastTableIndex();
        MigrationLog migrationLog = new MigrationLog();
        migrationLog.setSourceFileName(dataResource.getFile());
        migrationLog.setLastTableIndex(lastTableIndex + 1);
        migrationLog.setTableName(String.format(TABLE_NAME_FORMAT, SqlQueryHelper.normalizeName(dataResource.getFile()),
                migrationLog.getLastTableIndex()));
        migrationLog.setMigrationStatus(MigrationStatus.IN_PROGRESS);
        migrationLog.setMigrationLogSource(migrationLogSource);
        migrationLog.setStartDate(LocalDateTime.now());
        return migrationLogRepository.save(migrationLog);
    }
}
