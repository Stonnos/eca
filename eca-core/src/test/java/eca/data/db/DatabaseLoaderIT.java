package eca.data.db;

import eca.config.DbSelectQueryTestData;
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
                for (DbSelectQueryTestData dbSelectQueryTestData : value.getSelectQueries()) {
                    executeQuery(dbSelectQueryTestData);
                }
            } catch (Exception ex) {
                fail(ex);
            } finally {
                close();
            }
        });
    }

    private void executeQuery(DbSelectQueryTestData dbSelectQueryTestData) throws Exception {
        jdbcQueryExecutor.setSource(dbSelectQueryTestData.getSqlQuery());
        Instances actual = jdbcQueryExecutor.loadInstances();
        Instances expected = loadInstances(dbSelectQueryTestData.getExpectedDataFile());
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
