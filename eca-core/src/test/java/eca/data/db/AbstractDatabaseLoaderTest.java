package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.config.DbSelectQueryTestData;
import eca.data.db.model.DataBaseType;
import weka.core.Instances;

import java.sql.SQLException;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Abstract class for database loader tests.
 *
 * @author Roman Batygin
 */
public abstract class AbstractDatabaseLoaderTest extends AbstractDatabaseTest {

    private final JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor();

    @Override
    void executeTest(DataBaseType dataBaseType, DatabaseTestConfig databaseTestConfig) {
        ConnectionDescriptor connectionDescriptor = createConnectionDescriptor(dataBaseType, databaseTestConfig);
        jdbcQueryExecutor.setConnectionDescriptor(connectionDescriptor);
        try {
            jdbcQueryExecutor.open();
            for (DbSelectQueryTestData dbSelectQueryTestData : databaseTestConfig.getSelectQueries()) {
                executeQuery(dbSelectQueryTestData);
            }
        } catch (Exception ex) {
            fail(ex);
        } finally {
            close();
        }
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
