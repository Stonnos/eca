package eca.data.file.csv;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import weka.core.Instances;
import weka.core.converters.CSVSaver;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Implements saving data into csv file.
 *
 * @author Roman Batygin
 */
public class CsvSaver extends AbstractDataSaver {

    /**
     * Default constructor.
     */
    public CsvSaver() {
        super(Collections.singleton(DataFileExtension.CSV.getExtendedExtension()));
    }

    @Override
    protected void internalWrite(Instances data, File file) throws Exception {
        CSVSaver csvSaver = new CSVSaver();
        csvSaver.setFile(file);
        csvSaver.setInstances(data);
        csvSaver.writeBatch();
    }

    @Override
    public void write(Instances data, OutputStream outputStream) throws Exception {
        CSVSaver csvSaver = new CSVSaver();
        csvSaver.setDestination(outputStream);
        csvSaver.setInstances(data);
        csvSaver.writeBatch();
    }
}
