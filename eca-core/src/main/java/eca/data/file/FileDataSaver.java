package eca.data.file;

import eca.data.DataFileExtension;
import eca.data.FileUtils;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.JSONSaver;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for saving {@link Instances} objects to file with extensions such as:
 * csv, arff, xls, xlsx, json.
 *
 * @author Roman Batygin
 */
public class FileDataSaver {

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns date format.
     *
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Objects.requireNonNull(dateFormat, "Date format is not specified!");
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
        Objects.requireNonNull(file, "File is not specified!");
        Objects.requireNonNull(data, "Data is not specified!");
        if (FileUtils.isXlsExtension(file.getName())) {
            XLSSaver xlsSaver = new XLSSaver();
            xlsSaver.setFile(file);
            xlsSaver.setDateFormat(dateFormat);
            xlsSaver.write(data);
        } else {
            AbstractFileSaver abstractFileSaver = createFileSaver(file, data);
            abstractFileSaver.setFile(file);
            abstractFileSaver.setInstances(data);
            abstractFileSaver.writeBatch();
        }
    }

    private AbstractFileSaver createFileSaver(File file, Instances data) {
        String fileName = file.getName();
        AbstractFileSaver abstractFileSaver;
        if (fileName.endsWith(DataFileExtension.CSV.getExtension())) {
            abstractFileSaver = new CSVSaver();
        } else if (fileName.endsWith(DataFileExtension.ARFF.getExtension())) {
            abstractFileSaver = new ArffSaver();
        } else if (fileName.endsWith(DataFileExtension.JSON.getExtension())) {
            abstractFileSaver = new JSONSaver();
        } else {
            throw new IllegalArgumentException(
                    String.format("Can't save data %s to file '%s'", data.relationName(), file.getAbsoluteFile()));
        }
        return abstractFileSaver;
    }
}
