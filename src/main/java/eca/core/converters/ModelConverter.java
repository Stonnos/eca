package eca.core.converters;

import eca.io.SerializedObject;

import java.io.File;

/**
 * Implements saving and loading serialized object from file.
 * @author Roman Batygin
 */
public class ModelConverter {

    /**
     * Serialise and saves given object to file.
     * @param file file object
     * @param model object
     * @throws Exception
     */
    public static void saveModel(File file, Object model) throws Exception {
        if (!file.getName().endsWith(".txt")) {
            throw new Exception("Wrong file extension!");
        }
        SerializedObject.serialize(model, file.getAbsolutePath());
    }

    /**
     * Loads serialized object from file.
     * @param file file object
     * @return deserialized object
     * @throws Exception
     */
    public static Object loadModel(File file) throws Exception {
        if (!file.getName().endsWith(".txt"))
            throw new Exception("Wrong file extension!");
        return SerializedObject.deserialize(file.getAbsolutePath());
    }
}
