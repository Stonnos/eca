package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.file.resource.FileResource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import weka.core.Instances;

import java.io.File;

import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.loadInstances;

/**
 * Base file loader test.
 *
 * @author Roman Batygin
 */
public abstract class BaseFileLoaderTest {

    private static final String DATA_CREDIT_ARFF = "data/credit-g.arff";

    private Instances expected;

    @BeforeEach
    void init() {
        expected = loadInstances(DATA_CREDIT_ARFF);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void executeTest(AbstractDataLoader dataLoader, String testFile) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(testFile).getFile())));
        Instances actual = dataLoader.loadInstances();
        assertInstances(expected, actual);
    }
}
