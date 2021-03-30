package eca.data.file.xml;

import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link XmlSaver} class.
 *
 * @author Roman Batygin
 */
class XmlSaverTest extends BaseFileSaverTest {

    private final XmlSaver dataSaver = new XmlSaver();

    @Test
    void testSaveDataToXmlFile() {
        executeTest(dataSaver);
    }

    @Override
    public List<DataFileExtension> getFileExtensions() {
        return Collections.singletonList(DataFileExtension.XML);
    }
}
