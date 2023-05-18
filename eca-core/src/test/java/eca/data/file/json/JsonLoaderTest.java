package eca.data.file.json;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link JsonLoader} class.
 *
 * @author Roman Batygin
 */
class JsonLoaderTest extends BaseFileLoaderTest {

    private static final String DATA_CREDIT_JSON = "data/credit.json";
    private static final String DATA_IRIS_WITH_INVALID_CLASS_JSON = "data/iris-with-invalid-class.json";

    private final JsonLoader jsonLoader = new JsonLoader();

    @Test
    void testLoadDataFromJsonFile() {
        executeTest(jsonLoader, DATA_CREDIT_JSON);
    }

    @Test
    void testLoadDataWithInvalidClassName() {
        assertThrows(IllegalStateException.class, () -> executeTest(jsonLoader, DATA_IRIS_WITH_INVALID_CLASS_JSON));
    }
}
