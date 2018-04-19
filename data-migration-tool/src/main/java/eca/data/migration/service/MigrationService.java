package eca.data.migration.service;

import eca.data.file.FileDataLoader;
import eca.data.migration.exception.MigrationException;
import eca.data.migration.model.MigrationLog;
import eca.data.migration.model.MigrationLogSource;
import eca.data.migration.model.MigrationStatus;
import eca.data.migration.repository.MigrationLogRepository;
import eca.data.migration.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import weka.core.Instances;

import java.io.File;
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

    private final FileDataLoader dataLoader;
    private final InstancesService instancesService;
    private final MigrationLogRepository migrationLogRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param dataLoader             - data loader bean
     * @param instancesService       - instances service bean
     * @param migrationLogRepository - migration log repository bean
     */
    public MigrationService(FileDataLoader dataLoader, InstancesService instancesService,
                            MigrationLogRepository migrationLogRepository) {
        this.dataLoader = dataLoader;
        this.instancesService = instancesService;
        this.migrationLogRepository = migrationLogRepository;
    }

    /**
     * Migrates training data file into database.
     *
     * @param file - training data file
     */
    public void migrateData(File file) {
        dataLoader.setSource(file);
        log.info("Starting to migrate file '{}'.", file.getAbsolutePath());
        String tableName = String.format(TABLE_NAME_FORMAT, Utils.normalizeName(file.getName()), System.currentTimeMillis());
        MigrationLog migrationLog = createMigrationLog(file, tableName);
        migrationLogRepository.save(migrationLog);
        try {
            Instances instances = dataLoader.loadInstances();
            log.info("Data has been loaded from file '{}'", file.getAbsolutePath());
            instancesService.migrateInstances(tableName, instances);
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

    private MigrationLog createMigrationLog(File file, String tableName) {
        MigrationLog migrationLog = new MigrationLog();
        migrationLog.setSourceFileName(file.getName());
        migrationLog.setTableName(tableName);
        migrationLog.setMigrationStatus(MigrationStatus.IN_PROGRESS);
        migrationLog.setMigrationLogSource(MigrationLogSource.JOB);
        migrationLog.setStartDate(LocalDateTime.now());
        return migrationLog;
    }
}
