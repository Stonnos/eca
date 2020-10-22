package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.data.db.model.DataBaseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link JdbcQueryExecutor} class.
 *
 * @author Roman Batygin
 */
class JdbcQueryExecutorTest extends AbstractDatabaseLoaderTest {

    @Test
    void testLoadDataFromDatabase() {
        DatabaseTestConfig databaseTestConfig = getDatabaseTestConfigMap().get(DataBaseType.SQLITE);
        assertNotNull(databaseTestConfig);
        executeTest(DataBaseType.SQLITE, databaseTestConfig);
    }
}
