package eca.core.converters;

import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.io.IOException;

/**
 * Class for saving {@link Instances} objects to file with extensions such as:
 * csv, arff, xls, xlsx.
 * @author Roman Batygin
 */
public class DataSaver {

    /**
     * Saves filteredData to file.
     * @param file file object
     * @param data <tt>Instances</tt> object
     * @throws IOException
     */
    public static void saveData(File file, Instances data) throws IOException {
        String name = file.getName();
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            XLSSaver saver = new XLSSaver();
            saver.setFile(file);
            saver.write(data);
        } else {
            AbstractFileSaver saver;
            if (name.endsWith(".csv")) {
                saver = new CSVSaver();
            } else if (name.endsWith(".arff")) {
                saver = new ArffSaver();
            } else {
                throw new IOException("Wrong file extension!");
            }
            saver.setFile(file);
            saver.setInstances(data);
            saver.writeBatch();
        }
    }
}
