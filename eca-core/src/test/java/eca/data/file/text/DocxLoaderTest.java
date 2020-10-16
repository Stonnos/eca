package eca.data.file.text;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DocxLoader} class.
 *
 * @author Roman Batygin
 */
class DocxLoaderTest extends BaseFileLoaderTest {

    private static final String DATA_CREDIT_DOCX = "data/credit.docx";

    private final DocxLoader dataLoader = new DocxLoader();

    @Test
    void testLoadDataFromDocxFile() throws Exception {
        executeTest(dataLoader, DATA_CREDIT_DOCX);
    }
}
