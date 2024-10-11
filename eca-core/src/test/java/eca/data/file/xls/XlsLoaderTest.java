package eca.data.file.xls;

import eca.data.file.BaseFileLoaderTest;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link XLSLoader} class.
 *
 * @author Roman Batygin
 */
class XlsLoaderTest extends BaseFileLoaderTest {
    private static final String DATA_CREDIT_XLSX = "data/credit.xlsx";

    private final XLSLoader xlsLoader = new XLSLoader();

    @Test
    void testLoadDataFromXlsxFile() {
        executeTest(xlsLoader, DATA_CREDIT_XLSX);
    }
}
