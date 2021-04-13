package eca.data.file.text;

import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link DocxSaver} class.
 *
 * @author Roman Batygin
 */
class DocxSaverTest extends BaseFileSaverTest {

    private final DocxSaver dataSaver = new DocxSaver();

    @Test
    void testSaveDataToTxtFile() {
        executeTest(dataSaver);
    }

    @Override
    public List<DataFileExtension> getFileExtensions() {
        return Collections.singletonList(DataFileExtension.DOCX);
    }
}
