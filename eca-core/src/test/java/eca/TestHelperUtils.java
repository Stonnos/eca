package eca;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.core.evaluation.Evaluation;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final String USER_DIR = "user.dir";
    private static final String TARGET = "/target/";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Load json config from specified file.
     *
     * @param fileName       - file name
     * @param tTypeReference - object type reference
     * @param <T>            - config generic type
     * @return config object
     */
    @SneakyThrows
    public static <T> T loadConfig(String fileName, TypeReference<T> tTypeReference) {
        @Cleanup InputStream inputStream = TestHelperUtils.class.getClassLoader().getResourceAsStream(fileName);
        return OBJECT_MAPPER.readValue(inputStream, tTypeReference);
    }

    /**
     * Loads instances from file.
     *
     * @return instances object
     */
    @SneakyThrows
    public static Instances loadInstances(String fileName) {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        FileDataLoader dataLoader = new FileDataLoader();
        dataLoader.setSource(new FileResource(new File(classLoader.getResource(fileName).getFile())));
        return dataLoader.loadInstances();
    }

    /**
     * Builds and evaluate model using training data evaluation method.
     *
     * @param instances  - instances object
     * @param classifier - classifier object
     * @return evaluation object
     */
    @SneakyThrows
    public static Evaluation buildAndEvaluateModel(Instances instances, Classifier classifier) {
        classifier.buildClassifier(instances);
        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(classifier, instances);
        return evaluation;
    }

    /**
     * Gets target directory path.
     *
     * @return target directory path
     */
    public static String getTargetPath() {
        return System.getProperty(USER_DIR) + TARGET;
    }

    /**
     * Copy file from resource into target directory.
     *
     * @param file - file name
     * @throws IOException in case of I/O error
     */
    public static void copyResource(String file) throws IOException {
        @Cleanup InputStream inputStream = TestHelperUtils.class.getClassLoader().getResourceAsStream(file);
        String target = String.format("%s%s", getTargetPath(), file);
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(target);
        IOUtils.copy(inputStream, fileOutputStream);
    }

}
