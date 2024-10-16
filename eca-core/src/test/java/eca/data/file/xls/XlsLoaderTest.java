package eca.data.file.xls;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link XLSLoader} class.
 *
 * @author Roman Batygin
 */
class XlsLoaderTest extends BaseFileLoaderTest {
    private static final String DATA_CREDIT_XLSX = "data/credit.xlsx";
    private static final String INVALID_DATA_DIFFERENT_CELL_TYPES_XLSX = "data/invalid_data_different_cell_types.xlsx";

    private static final String INVALID_DATA_EMPTY_HEADER_XLSX = "data/invalid_data_empty_header.xlsx";

    private final XLSLoader xlsLoader = new XLSLoader();

    @Test
    void testLoadDataFromXlsxFile() {
        executeTest(xlsLoader, DATA_CREDIT_XLSX);
    }

    @Test
    void testLoadDataFromXlsxFileWithDifferentCellTypesShouldThrowError() {
        assertThrows(IllegalArgumentException.class,
                () -> executeTest(xlsLoader, INVALID_DATA_DIFFERENT_CELL_TYPES_XLSX));
    }

    @Test
    void testLoadDataFromXlsxFileWithEmptyHeaderShouldThrowError() {
        assertThrows(IllegalArgumentException.class,
                () -> executeTest(xlsLoader, INVALID_DATA_EMPTY_HEADER_XLSX));
    }
}
