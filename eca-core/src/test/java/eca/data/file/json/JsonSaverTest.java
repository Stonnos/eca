package eca.data.file.json;

import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link JsonSaver} class.
 *
 * @author Roman Batygin
 */
class JsonSaverTest extends BaseFileSaverTest {

    private final JsonSaver jsonSaver = new JsonSaver();

    @Test
    void testSaveDataToJsonFile() {
        executeTest(jsonSaver);
    }

    @Override
    public List<DataFileExtension> getFileExtensions() {
        return Collections.singletonList(DataFileExtension.JSON);
    }
}
