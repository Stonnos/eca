package eca.data.file.text;

import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link DATASaver} class.
 *
 * @author Roman Batygin
 */
class DATASaverTest extends BaseFileSaverTest {

    private final DATASaver dataSaver = new DATASaver();

    @Test
    void testSaveDataToTxtFile() {
        executeTest(dataSaver);
    }

    @Override
    public String getFileExtension() {
        return DataFileExtension.TEXT.getExtension();
    }
}
