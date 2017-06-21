package eca.core.converters;

import eca.io.SerializedObject;

import java.io.File;

/**
 * @author Roman Batygin
 */

public class ModelConverter {

    public static void saveModel(File file, Object model) throws Exception {
        if (!file.getName().endsWith(".txt")) {
            throw new Exception("Wrong file extension!");
        }
        SerializedObject.serialize(model, file.getAbsolutePath());
    }

    public static Object loadModel(File file) throws Exception {
        if (!file.getName().endsWith(".txt"))
            throw new Exception("Wrong file extension!");
        return SerializedObject.deserialize(file.getAbsolutePath());
    }
}
