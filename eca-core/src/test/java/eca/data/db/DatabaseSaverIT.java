package eca.data.db;

import eca.config.DatabaseTestConfig;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.IOException;
import java.sql.SQLException;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.copyResource;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for saving data into different databases.
 *
 * @author Roman Batygin
 */
class DatabaseSaverIT extends BaseDatabaseTest {

    private static final String TABLE_NAME_FORMAT = "data_%d";
    private static final String SELECT_QUERY = "select * from %s";

    private final JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor();

    @Test
    void testSaveDataIntoDatabase() {
        Instances instances = loadInstances("data/evaluation_log.xlsx");
        getDatabaseTestConfigMap().forEach((key, value) -> {
            ConnectionDescriptor connectionDescriptor = createConnectionDescriptor(key, value);
            executeNextTest(instances, connectionDescriptor);
        });
    }

    @Override
    ConnectionDescriptor createConnectionDescriptor(DataBaseType dataBaseType, DatabaseTestConfig databaseTestConfig) {
        ConnectionDescriptor connectionDescriptor = super.createConnectionDescriptor(dataBaseType, databaseTestConfig);
        if (connectionDescriptor.getDataBaseType().isEmbedded()) {
            connectionDescriptor.setHost(getTargetPath());
        }
        return connectionDescriptor;
    }

    private void executeNextTest(Instances expected, ConnectionDescriptor connectionDescriptor) {
        try {
            DatabaseSaver databaseSaver = initializeDatabaseSaver(connectionDescriptor);
            databaseSaver.write(expected);
            jdbcQueryExecutor.setConnectionDescriptor(connectionDescriptor);
            jdbcQueryExecutor.setSource(String.format(SELECT_QUERY, databaseSaver.getTableName()));
            jdbcQueryExecutor.open();
            Instances actual = jdbcQueryExecutor.loadInstances();
            assertInstances(expected, actual);
        } catch (Exception ex) {
            fail(ex);
        } finally {
            close();
        }
    }

    private DatabaseSaver initializeDatabaseSaver(ConnectionDescriptor connectionDescriptor) throws IOException {
        DatabaseSaver databaseSaver = new DatabaseSaver(connectionDescriptor);
        databaseSaver.setTableName(String.format(TABLE_NAME_FORMAT, System.currentTimeMillis()));
        if (DataBaseType.MS_ACCESS.equals(connectionDescriptor.getDataBaseType())) {
            copyResource(connectionDescriptor.getDataBaseName());
        }
        return databaseSaver;
    }

    private void close() {
        try {
            jdbcQueryExecutor.close();
        } catch (SQLException ex) {
            fail(ex);
        }
    }
}
