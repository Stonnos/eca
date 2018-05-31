package eca.data;

import weka.core.Instances;

import java.io.IOException;

/**
 * Data saver interface.
 *
 * @author Roman Batygin
 */
public interface DataSaver {

    /**
     * Saves instances.
     *
     * @param data - instances
     */
    void write(Instances data) throws IOException;
}
