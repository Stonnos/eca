package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.config.DbTestCase;
import eca.config.EcaCoreTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.InstanceComparator;
import weka.core.Instances;

import java.util.List;
import java.util.Map;

import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration tests for loading data from different databases.
 *
 * @author Roman Batygin
 */
class DatabaseLoaderIT {

    private final EcaCoreTestConfiguration ecaCoreTestConfiguration = EcaCoreTestConfiguration.getInstance();

    private final JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor();

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
            performDatabaseTests(value.getTestCases());
        });
    }

    private void performDatabaseTests(List<DbTestCase> dbTestCases) {
        try {
            for (DbTestCase dbTestCase : dbTestCases) {
                jdbcQueryExecutor.setSource(dbTestCase.getSqlQuery());
                Instances actual = jdbcQueryExecutor.loadInstances();
                Instances expected = loadInstances(dbTestCase.getExpectedDataFile());
            }
        } catch (Exception ex) {
            fail(ex);
        }
    }

    private ConnectionDescriptor createConnectionDescriptor(DataBaseType dataBaseType,
                                                            DatabaseTestConfig databaseTestConfig) {
        return dataBaseType.handle(new DataBaseTypeVisitor<ConnectionDescriptor>() {
            @Override
            public ConnectionDescriptor caseMySql() {
                return null;
            }

            @Override
            public ConnectionDescriptor casePostgreSQL() {
                return null;
            }

            @Override
            public ConnectionDescriptor caseMSAccess() {
                return null;
            }

            @Override
            public ConnectionDescriptor caseMSSQL() {
                return null;
            }

            @Override
            public ConnectionDescriptor caseSQLite() {
                return null;
            }
        });
    }
}
