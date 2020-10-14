package eca;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

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
            throw new IllegalStateException(ex);
        }
    }
}
