package eca;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.file.FileDataLoader;
import eca.data.file.resource.FileResource;
import lombok.experimental.UtilityClass;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test helper utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class TestHelperUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Load json config from specified file.
     *
     * @param fileName       - file name
     * @param tTypeReference - object type reference
     * @param <T>            - config generic type
     * @return config object
     */
    public static <T> T loadConfig(String fileName, TypeReference<T> tTypeReference) {
        try (InputStream inputStream = TestHelperUtils.class.getClassLoader().getResourceAsStream(fileName)) {
            return OBJECT_MAPPER.readValue(inputStream, tTypeReference);
        } catch (IOException ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Loads instances from file.
     *
     * @return instances object
     */
    public static Instances loadInstances(String fileName) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            FileDataLoader dataLoader = new FileDataLoader();
            dataLoader.setSource(new FileResource(new File(classLoader.getResource(fileName).getFile())));
            return dataLoader.loadInstances();
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

}
