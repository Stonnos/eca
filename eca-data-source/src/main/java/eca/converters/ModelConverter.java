package eca.converters;

import eca.utils.SerializationUtils;
import org.springframework.util.Assert;

import java.io.File;

/**
 * Implements saving and loading serialized object from file.
 *
 * @author Roman Batygin
 */
public class ModelConverter {

    /**
     * Serialise and saves given object to file.
     *
     * @param file  file object
     * @param model object
     * @throws Exception
     */
    public static void saveModel(File file, Object model) throws Exception {
        Assert.notNull(file, "File is not specified!");
        Assert.notNull(model, "Object is not specified!");
        if (!file.getName().endsWith(DataFileExtension.TXT)) {
            throw new Exception(String.format("Can't save object %s to file '%s'",
                    model, file.getAbsoluteFile()));
        }
        SerializationUtils.serialize(model, file.getAbsolutePath());
    }

    /**
     * Loads serialized object from file.
     *
     * @param file file object
     * @return deserialize object
     * @throws Exception
     */
    public static Object loadModel(File file) throws Exception {
        Assert.notNull(file, "File is not specified!");
        if (!file.getName().endsWith(DataFileExtension.TXT)) {
            throw new Exception(String.format("Can't load object from file '%s'", file.getAbsoluteFile()));
        }
        return SerializationUtils.deserialize(file.getAbsolutePath());
    }
}
