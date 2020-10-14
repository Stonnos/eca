package eca.client;

import eca.data.file.resource.FileResource;
import eca.data.file.xls.XLSLoader;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.io.File;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String DATA_PATH = "iris.xls";

    /**
     * Loads instances from file.
     *
     * @return instances object
     */
    public static Instances loadInstances() {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            XLSLoader dataLoader = new XLSLoader();
            dataLoader.setSource(new FileResource(new File(classLoader.getResource(DATA_PATH).getFile())));
            return dataLoader.loadInstances();
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

}
