package eca.data.migration;

import eca.data.file.xls.XLSLoader;
import eca.data.file.resource.UrlResource;
import weka.core.Instances;

import java.io.InputStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
public class TestHelperUtils {

    public static final String DATA_PATH = "german_credit.xls";

    /**
     * Loads test data set.
     *
     * @return created training data
     */
    public static Instances loadInstances() throws Exception {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(DATA_PATH)) {
            XLSLoader xlsLoader = new XLSLoader();
            //xlsLoader.setResource(new UrlResource(inputStream));
            return xlsLoader.getDataSet();
        }
    }
}
