package eca.data.file.text;

import com.google.common.collect.ImmutableList;
import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    public List<DataFileExtension> getFileExtensions() {
        return ImmutableList.of(DataFileExtension.TEXT, DataFileExtension.DATA);
    }
}
