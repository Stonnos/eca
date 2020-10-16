package eca.data.file.xls;

import com.google.common.collect.ImmutableList;
import eca.data.DataFileExtension;
import eca.data.file.BaseFileSaverTest;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for {@link XLSSaver} class.
 *
 * @author Roman Batygin
 */
class XlsSaverTest extends BaseFileSaverTest {

    private final XLSSaver dataSaver = new XLSSaver();

    @Test
    void testSaveDataToXlsxFile() {
        executeTest(dataSaver);
    }

    @Override
    public List<DataFileExtension> getFileExtensions() {
        return ImmutableList.of(DataFileExtension.XLSX, DataFileExtension.XLS);
    }
}
