package eca.data.db;

import eca.config.DbTestData;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.sql.SQLException;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for loading data from different databases.
 *
 * @author Roman Batygin
 */
class DatabaseLoaderIT extends BaseDatabaseTest {

    private final JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor();

    @Test
    void testLoadDataFromDataBase() {
        getDatabaseTestConfigMap().forEach((key, value) -> {
            ConnectionDescriptor connectionDescriptor = createConnectionDescriptor(key, value);
            jdbcQueryExecutor.setConnectionDescriptor(connectionDescriptor);
            try {
                jdbcQueryExecutor.open();
                for (DbTestData dbTestData : value.getDbTestDataList()) {
                    executeQuery(dbTestData);
                }
            } catch (Exception ex) {
                fail(ex);
            } finally {
                close();
            }
        });
    }

    private void executeQuery(DbTestData dbTestData) throws Exception {
        jdbcQueryExecutor.setSource(dbTestData.getSqlQuery());
        Instances actual = jdbcQueryExecutor.loadInstances();
        Instances expected = loadInstances(dbTestData.getExpectedDataFile());
        assertInstances(expected, actual);
    }

    private void close() {
        try {
            jdbcQueryExecutor.close();
        } catch (SQLException ex) {
            fail(ex);
        }
    }
}
