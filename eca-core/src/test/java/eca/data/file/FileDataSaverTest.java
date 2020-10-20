package eca.data.file;

import eca.data.DataFileExtension;
import eca.data.file.resource.FileResource;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.io.File;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.DATA_CREDIT_ARFF;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;

/**
 * Unit tests for {@link FileDataSaver} class.
 *
 * @author Roman Batygin
 */
class FileDataSaverTest {

    private static final String DATA_FORMAT = "data-%d.%s";

    private FileDataSaver fileDataSaver = new FileDataSaver();
    private FileDataLoader fileDataLoader = new FileDataLoader();

    private Instances expected;

    @BeforeEach
    void init() {
        expected = loadInstances(DATA_CREDIT_ARFF);
    }

    @Test
    void testSaveDataIntoFile() throws Exception {
        for (DataFileExtension extension : DataFileExtension.values()) {
            File file = new File(getTargetPath(),
                    String.format(DATA_FORMAT, System.currentTimeMillis(), extension.getExtension()));
            fileDataSaver.saveData(file, expected);
            fileDataLoader.setSource(new FileResource(file));
            Instances actual = fileDataLoader.loadInstances();
            assertInstances(expected, actual);
            FileUtils.deleteQuietly(file);
        }
    }
}
