package eca.data;

import weka.core.Instances;

import java.io.File;
import java.io.OutputStream;

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

    /**
     * Saves instances to output stream.
     *
     * @param data - instances
     * @param outputStream - output stream
     */
    void write(Instances data, OutputStream outputStream) throws Exception;
}
