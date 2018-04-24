package eca.data.migration.service;

import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import eca.data.migration.TestHelperUtils;
import eca.data.migration.exception.MigrationException;
import eca.data.migration.model.entity.MigrationLog;
import eca.data.migration.model.entity.MigrationLogSource;
import eca.data.migration.model.entity.MigrationStatus;
import eca.data.migration.repository.MigrationLogRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;
import javax.inject.Inject;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Unit tests for checking {@link MigrationService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MigrationServiceTest {

    @Mock
    private FileDataLoader dataLoader;
    @Inject
    private MigrationLogRepository migrationLogRepository;
    @Mock
    private InstancesService instancesService;

    private MigrationService migrationService;

    private Instances instances;

    @Before
    public void init() throws Exception {
        migrationLogRepository.deleteAll();
        migrationService = new MigrationService(dataLoader, instancesService, migrationLogRepository);
        instances = TestHelperUtils.loadInstances();
    }

    @Test
    public void testSuccessMigration() throws Exception {
        when(dataLoader.loadInstances()).thenReturn(instances);
        doNothing().when(instancesService).migrateInstances(anyString(), any(Instances.class));
        migrationService.migrateData(new FileResource(new File(TestHelperUtils.DATA_PATH)), MigrationLogSource.JOB);
        MigrationLog migrationLog = migrationLogRepository.findAll().stream().findFirst().orElse(null);
        Assertions.assertThat(migrationLog).isNotNull();
        Assertions.assertThat(migrationLog.getMigrationStatus()).isEqualTo(MigrationStatus.SUCCESS);
        Assertions.assertThat(migrationLog.getTableName()).isNotNull();
        Assertions.assertThat(migrationLog.getFinishDate()).isNotNull();
    }

    @Test(expected = MigrationException.class)
    public void testErrorMigration() throws Exception {
        when(dataLoader.loadInstances()).thenThrow(new MigrationException("There was as error!"));
        migrationService.migrateData(new FileResource(new File(TestHelperUtils.DATA_PATH)), MigrationLogSource.JOB);
    }
}
