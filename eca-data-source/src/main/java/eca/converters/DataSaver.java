package eca.converters;

import org.springframework.util.Assert;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.io.IOException;

/**
 * Class for saving {@link Instances} objects to file with extensions such as:
 * csv, arff, xls, xlsx.
 *
 * @author Roman Batygin
 */
public class DataSaver {

    /**
     * Saves data to file.
     *
     * @param file file object
     * @param data <tt>Instances</tt> object
     * @throws IOException
     */
    public static void saveData(File file, Instances data) throws IOException {
        Assert.notNull(file, "File is not specified!");
        Assert.notNull(data, "Data is not specified!");
        String name = file.getName();
        if (name.endsWith(DataFileExtension.XLS) || name.endsWith(DataFileExtension.XLSX)) {
            XLSSaver saver = new XLSSaver();
            saver.setFile(file);
            saver.write(data);
        } else {
            AbstractFileSaver saver;
            if (name.endsWith(DataFileExtension.CSV)) {
                saver = new CSVSaver();
            } else if (name.endsWith(DataFileExtension.ARFF)) {
                saver = new ArffSaver();
            } else {
                throw new IOException(String.format("Can't save data %s to file '%s'",
                        data.relationName(), file.getAbsoluteFile()));
            }
            saver.setFile(file);
            saver.setInstances(data);
            saver.writeBatch();
        }
    }
}
