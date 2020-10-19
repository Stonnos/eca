package eca.data.db;

import org.junit.jupiter.api.Test;

/**
 * Integration tests for loading data from different databases.
 *
 * @author Roman Batygin
 */
class DatabaseLoaderIT extends AbstractDatabaseLoaderTest {

    @Test
    void testLoadDataFromDataBase() {
        getDatabaseTestConfigMap().forEach(this::executeTest);
    }
}
