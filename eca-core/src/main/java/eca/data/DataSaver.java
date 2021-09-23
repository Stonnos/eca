package eca.data;

import weka.core.Instances;

import java.io.File;

/**
 * Data saver interface.
 *
 * @author Roman Batygin
 */
public interface DataSaver {

    /**
     * Saves instances to specified file.
     *
     * @param data - instances
     * @param file - file
     */
    void write(Instances data, File file) throws Exception;
}
