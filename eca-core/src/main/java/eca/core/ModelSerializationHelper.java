package eca.core;

import eca.data.file.resource.DataResource;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.SerializationUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Implements saving and loading serialized object from file.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ModelSerializationHelper {

    private static final String MODEL_EXTENSION = ".model";

    /**
     * Serializes and saves given object to file.
     *
     * @param targetFile - target file
     * @param model      - model object
     * @throws FileNotFoundException in case of file not found error
     */
    public static void serialize(File targetFile, Serializable model) throws IOException {
        Objects.requireNonNull(targetFile, "File is not specified!");
        Objects.requireNonNull(model, "Object is not specified!");
        if (!targetFile.getName().endsWith(MODEL_EXTENSION)) {
            throw new IllegalArgumentException(String.format("Can't save object %s to file '%s'",
                    model, targetFile.getAbsoluteFile()));
        }
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream(targetFile.getAbsoluteFile());
        SerializationUtils.serialize(model, fileOutputStream);
    }

    /**
     * Loads serialized model from file.
     *
     * @param dataResource - data resource object
     * @return deserialize object
     * @throws IOException in case of I/O error
     */
    public static <T> T deserialize(DataResource<?> dataResource, Class<T> targetClazz) throws IOException {
        Objects.requireNonNull(dataResource, "Data resource is not specified!");
        if (!dataResource.getFile().endsWith(MODEL_EXTENSION)) {
            throw new IllegalArgumentException(
                    String.format("Can't load model from file '%s'", dataResource.getFile()));
        }
        @Cleanup InputStream inputStream = dataResource.openInputStream();
        Object result = SerializationUtils.deserialize(inputStream);
        return targetClazz.cast(result);
    }
}
