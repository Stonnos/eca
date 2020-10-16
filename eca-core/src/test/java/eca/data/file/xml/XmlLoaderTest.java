package eca.data.file.xml;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link XmlLoader} class.
 *
 * @author Roman Batygin
 */
class XmlLoaderTest extends BaseFileLoaderTest {

    private static final String DATA_CREDIT_XML = "data/credit.xml";

    private final XmlLoader dataLoader = new XmlLoader();

    @Test
    void testLoadDataFromXmlFile() {
        executeTest(dataLoader, DATA_CREDIT_XML);
    }
}
