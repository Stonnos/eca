package eca.data.file;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import eca.data.file.json.JsonSaver;
import eca.data.file.text.DATASaver;
import eca.data.file.text.DocxSaver;
import eca.data.file.xls.XLSSaver;
import eca.data.file.xml.XmlSaver;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Class for saving {@link Instances} objects to file with extensions such as:
 * csv, arff, xls, xlsx, json, txt, data.
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
     * @param file - file object
     * @param data - instances object
     * @throws IOException in case of error
     */
    public void saveData(File file, Instances data) throws Exception {
        Objects.requireNonNull(file, "File is not specified!");
        Objects.requireNonNull(data, "Data is not specified!");
        if (FileUtils.isXlsExtension(file.getName())) {
            writeData(new XLSSaver(), file, data);
        } else if (FileUtils.isTxtExtension(file.getName())) {
            writeData(new DATASaver(), file, data);
        } else if (FileUtils.isDocxExtension(file.getName())) {
            writeData(new DocxSaver(), file, data);
        } else if (file.getName().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            writeData(new XmlSaver(), file, data);
        } else if (file.getName().endsWith(DataFileExtension.JSON.getExtendedExtension())) {
            writeData(new JsonSaver(), file, data);
        } else {
            AbstractFileSaver abstractFileSaver = createWekaFileSaver(file, data);
            abstractFileSaver.setFile(file);
            abstractFileSaver.setInstances(data);
            abstractFileSaver.writeBatch();
        }
    }

    private void writeData(AbstractDataSaver saver, File file, Instances data) throws Exception {
        saver.setFile(file);
        saver.setDateFormat(dateFormat);
        saver.write(data);
    }

    private AbstractFileSaver createWekaFileSaver(File file, Instances data) {
        String fileName = file.getName();
        AbstractFileSaver abstractFileSaver;
        if (fileName.endsWith(DataFileExtension.CSV.getExtendedExtension())) {
            abstractFileSaver = new CSVSaver();
        } else if (fileName.endsWith(DataFileExtension.ARFF.getExtendedExtension())) {
            abstractFileSaver = new ArffSaver();
        } else {
            throw new IllegalArgumentException(
                    String.format("Can't save data %s to file '%s'", data.relationName(), file.getAbsoluteFile()));
        }
        return abstractFileSaver;
    }
}
