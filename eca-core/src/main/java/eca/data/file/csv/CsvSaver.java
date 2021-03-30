package eca.data.file.csv;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import weka.core.Instances;
import weka.core.converters.CSVSaver;

import java.io.File;

/**
 * Implements saving data into csv file.
 *
 * @author Roman Batygin
 */
public class CsvSaver extends AbstractDataSaver {

    @Override
    public void write(Instances data) throws Exception {
        CSVSaver csvSaver = new CSVSaver();
        csvSaver.setFile(getFile());
        csvSaver.setInstances(data);
        csvSaver.writeBatch();
    }

    @Override
    protected boolean isValidFile(File file) {
        return file.getName().endsWith(DataFileExtension.CSV.getExtendedExtension());
    }

}
