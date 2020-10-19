package eca.data.db;

import eca.config.DatabaseTestConfig;
import eca.data.db.model.DataBaseType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link DatabaseSaver} class.
 *
 * @author Roman Batygin
 */
public class DatabaseSaverTest extends AbstractDatabaseSaverTest {

    @Test
    void testSaveDataToDatabase() {
        DatabaseTestConfig databaseTestConfig = getDatabaseTestConfigMap().get(DataBaseType.SQLITE);
        assertNotNull(databaseTestConfig);
        executeTest(DataBaseType.SQLITE, databaseTestConfig);
    }
}
