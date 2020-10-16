package eca.data.file;

import eca.data.AbstractDataSaver;
import eca.data.file.resource.FileResource;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import weka.core.Instances;

import java.io.File;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;

/**
 * Base file saver test.
 *
 * @author Roman Batygin
 */
public abstract class BaseFileSaverTest {

    private static final String DATA_CREDIT_ARFF = "data/credit-g.arff";
    private static final String DATA_FORMAT = "data-%d.%s";

    private Instances expected;
    private File targetFile;

    @BeforeEach
    void init() {
        expected = loadInstances(DATA_CREDIT_ARFF);
        targetFile =
                new File(getTargetPath(), String.format(DATA_FORMAT, System.currentTimeMillis(), getFileExtension()));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void executeTest(AbstractDataSaver dataSaver) {
        dataSaver.setFile(targetFile);
        dataSaver.write(expected);
        FileDataLoader fileDataLoader = new FileDataLoader();
        fileDataLoader.setSource(new FileResource(targetFile));
        Instances actual = fileDataLoader.loadInstances();
        assertInstances(expected, actual);
    }

    @AfterEach
    void delete() {
        FileUtils.deleteQuietly(targetFile);
    }

    public abstract String getFileExtension();
}
