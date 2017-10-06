package eca.data;

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

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns date format.
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Assert.notNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Saves data to file.
     *
     * @param file file object
     * @param data <tt>Instances</tt> object
     * @throws IOException
     */
    public void saveData(File file, Instances data) throws IOException {
        Assert.notNull(file, "File is not specified!");
        Assert.notNull(data, "Data is not specified!");
        String name = file.getName();
        if (name.endsWith(DataFileExtension.XLS) || name.endsWith(DataFileExtension.XLSX)) {
            XLSSaver xlsSaver = new XLSSaver();
            xlsSaver.setFile(file);
            xlsSaver.setDateFormat(dateFormat);
            xlsSaver.write(data);
        } else {
            AbstractFileSaver abstractFileSaver;
            if (name.endsWith(DataFileExtension.CSV)) {
                abstractFileSaver = new CSVSaver();
            } else if (name.endsWith(DataFileExtension.ARFF)) {
                abstractFileSaver = new ArffSaver();
            } else {
                throw new IOException(String.format("Can't save data %s to file '%s'",
                        data.relationName(), file.getAbsoluteFile()));
            }
            abstractFileSaver.setFile(file);
            abstractFileSaver.setInstances(data);
            abstractFileSaver.writeBatch();
        }
    }
}
