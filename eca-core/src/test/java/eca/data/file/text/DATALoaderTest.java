package eca.data.file.text;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DATALoader} class.
 *
 * @author Roman Batygin
 */
class DATALoaderTest extends BaseFileLoaderTest {

    private static final String DATA_CREDIT_TXT = "data/credit.txt";
    private static final String DATA_CREDIT_DATA = "data/credit.data";

    private final DATALoader dataLoader = new DATALoader();

    @Test
    void testLoadDataFromTxtFile() throws Exception {
        executeTest(dataLoader, DATA_CREDIT_TXT);
    }

    @Test
    void testLoadDataFromDATAFile() throws Exception {
        executeTest(dataLoader, DATA_CREDIT_DATA);
    }
}
