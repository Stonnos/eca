package eca.data.file.json;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JsonLoader} class.
 *
 * @author Roman Batygin
 */
class JsonLoaderTest extends BaseFileLoaderTest {

    private static final String DATA_CREDIT_JSON = "data/credit.json";

    private final JsonLoader jsonLoader = new JsonLoader();

    @Test
    void testLoadDataFromJsonFile() {
        executeTest(jsonLoader, DATA_CREDIT_JSON);
    }
}
