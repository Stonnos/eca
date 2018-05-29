package eca.data.migration.service;

import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import eca.data.migration.TestHelperUtils;
import eca.data.migration.config.MigrationConfig;
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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
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
@AutoConfigureDataJpa
@EnableJpaRepositories(basePackageClasses = MigrationLogRepository.class)
@EntityScan(basePackageClasses = MigrationLog.class)
@EnableConfigurationProperties
@TestPropertySource("classpath:application.properties")
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({MigrationService.class, FileDataLoader.class})
@Import(MigrationConfig.class)
public class MigrationServiceTest {

    @Inject
    private MigrationConfig migrationConfig;
    @Inject
    private MigrationLogRepository migrationLogRepository;
    @Mock
    private InstancesService instancesService;

    private MigrationService migrationService;

    private Instances instances;

    @Before
    public void init() throws Exception {
        migrationLogRepository.deleteAll();
        migrationService = new MigrationService(migrationConfig, instancesService, migrationLogRepository);
        instances = TestHelperUtils.loadInstances();
    }

    @Test
    public void testSuccessMigration() throws Exception {
        FileDataLoader dataLoader = PowerMockito.mock(FileDataLoader.class);
        PowerMockito.whenNew(FileDataLoader.class).withNoArguments().thenReturn(dataLoader);
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
        FileDataLoader dataLoader = PowerMockito.mock(FileDataLoader.class);
        PowerMockito.whenNew(FileDataLoader.class).withNoArguments().thenReturn(dataLoader);
        when(dataLoader.loadInstances()).thenThrow(new MigrationException("There was as error!"));
        migrationService.migrateData(new FileResource(new File(TestHelperUtils.DATA_PATH)), MigrationLogSource.JOB);
    }
}
