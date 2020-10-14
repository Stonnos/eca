package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.config.DbTestCase;
import eca.config.EcaCoreTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.sql.SQLException;
import java.util.Map;

import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for loading data from different databases.
 *
 * @author Roman Batygin
 */
class DatabaseLoaderIT {

    private final EcaCoreTestConfiguration ecaCoreTestConfiguration = EcaCoreTestConfiguration.getInstance();

    private final JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor();

    private final ConnectionDescriptorBuilder connectionDescriptorBuilder = new ConnectionDescriptorBuilder();

    private Map<DataBaseType, DatabaseTestConfig> databaseTestConfigMap;

    @BeforeEach
    void init() {
        databaseTestConfigMap = ecaCoreTestConfiguration.getDatabaseTestConfigMap();
    }

    @Test
    void testLoadDataFromDataBase() {
        databaseTestConfigMap.forEach((key, value) -> {
            ConnectionDescriptor connectionDescriptor = createConnectionDescriptor(key, value);
            jdbcQueryExecutor.setConnectionDescriptor(connectionDescriptor);
            try {
                jdbcQueryExecutor.open();
                for (DbTestCase dbTestCase : value.getTestCases()) {
                    executeQuery(dbTestCase);
                }
            } catch (Exception ex) {
                fail(ex);
            } finally {
                close();
            }
        });
    }

    private void executeQuery(DbTestCase dbTestCase) throws Exception {
        jdbcQueryExecutor.setSource(dbTestCase.getSqlQuery());
        Instances actual = jdbcQueryExecutor.loadInstances();
        Instances expected = loadInstances(dbTestCase.getExpectedDataFile());
        assertNotNull(actual);
        assertEquals(expected.numInstances(), actual.numInstances());
        assertEquals(expected.numAttributes(), actual.numAttributes());
        assertEquals(expected.numClasses(), actual.numClasses());
    }

    private ConnectionDescriptor createConnectionDescriptor(DataBaseType dataBaseType,
                                                            DatabaseTestConfig databaseTestConfig) {
        ConnectionDescriptor connectionDescriptor = dataBaseType.handle(connectionDescriptorBuilder);
        connectionDescriptor.setDriver(databaseTestConfig.getDriver());
        connectionDescriptor.setDataBaseName(databaseTestConfig.getDataBaseName());
        connectionDescriptor.setHost(databaseTestConfig.getHost());
        connectionDescriptor.setLogin(databaseTestConfig.getLogin());
        connectionDescriptor.setPassword(databaseTestConfig.getPassword());
        if (databaseTestConfig.getPort() != null) {
            connectionDescriptor.setPort(databaseTestConfig.getPort());
        }
        return connectionDescriptor;
    }

    private void close() {
        try {
            jdbcQueryExecutor.close();
        } catch (SQLException ex) {
            fail(ex);
        }
    }
}
