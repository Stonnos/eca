package eca.data.file;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.resource.FileResource;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import weka.core.Instances;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static eca.AssertionUtils.assertInstances;
import static eca.TestHelperUtils.DATA_CREDIT_ARFF;
import static eca.TestHelperUtils.getTargetPath;
import static eca.TestHelperUtils.loadInstances;

/**
 * Base file saver test.
 *
 * @author Roman Batygin
 */
public abstract class BaseFileSaverTest {

    private static final String DATA_FORMAT = "data-%d.%s";

    private Instances expected;
    private List<File> targetFiles = newArrayList();

    @BeforeEach
    void init() {
        expected = loadInstances(DATA_CREDIT_ARFF);
        getFileExtensions().forEach(extension -> targetFiles.add(new File(getTargetPath(),
                String.format(DATA_FORMAT, System.currentTimeMillis(), extension.getExtension()))));
    }

    /**
     * Test save data into files with specified extensions.
     *
     * @param dataSaver - data saver
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void executeTest(AbstractDataSaver dataSaver) {
        for (File file : targetFiles) {
            dataSaver.setFile(file);
            dataSaver.write(expected);
            FileDataLoader fileDataLoader = new FileDataLoader();
            fileDataLoader.setSource(new FileResource(file));
            Instances actual = fileDataLoader.loadInstances();
            assertInstances(expected, actual);
        }
    }

    @AfterEach
    void delete() {
        targetFiles.forEach(FileUtils::deleteQuietly);
    }

    public abstract List<DataFileExtension> getFileExtensions();
}
