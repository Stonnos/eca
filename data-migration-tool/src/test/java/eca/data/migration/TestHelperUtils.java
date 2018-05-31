package eca.data.migration;

import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import weka.core.Instances;

import java.io.File;

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
        XLSLoader dataLoader = new XLSLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
        return dataLoader.loadInstances();
    }
}
