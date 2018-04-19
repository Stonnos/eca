package eca.data.migration.service;

import eca.data.migration.TestHelperUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import weka.core.Instances;

import javax.inject.Inject;

/**
 * Unit tests for checking {@link InstancesService} functionality.
 *
 * @author Roman Batygin
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InstancesServiceTest {

    private static final String TABLE_NAME = "test_table";
    private static final String SELECT_COUNT_FORMAT = "SELECT count(*) FROM %s";

    @Inject
    private InstancesService instancesService;
    @Inject
    private JdbcTemplate jdbcTemplate;

    private Instances instances;

    @Before
    public void init() throws Exception {
        instances = TestHelperUtils.loadInstances();
    }

    @Test
    public void testMigrateInstances() {
        instancesService.migrateInstances(TABLE_NAME, instances);
        Integer result = jdbcTemplate.queryForObject(String.format(SELECT_COUNT_FORMAT, TABLE_NAME), Integer.class);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(instances.numInstances());
    }
}
