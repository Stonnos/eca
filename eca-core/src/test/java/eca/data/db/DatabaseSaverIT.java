package eca.data.db;

import org.junit.jupiter.api.Test;

/**
 * Integration tests for saving data into different databases.
 *
 * @author Roman Batygin
 */
class DatabaseSaverIT extends AbstractDatabaseSaverTest {

    @Test
    void testSaveDataIntoDatabase() {
        getDatabaseTestConfigMap().forEach(this::executeTest);
    }
}
