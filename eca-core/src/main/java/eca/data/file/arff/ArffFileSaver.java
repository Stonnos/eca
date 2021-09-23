package eca.data.file.arff;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Implements saving data into arff file.
 *
 * @author Roman Batygin
 */
public class ArffFileSaver extends AbstractDataSaver {

    /**
     * Default constructor.
     */
    public ArffFileSaver() {
        super(Collections.singleton(DataFileExtension.ARFF.getExtendedExtension()));
    }

    @Override
    protected void internalWrite(Instances data, File file) throws Exception {
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setFile(file);
        arffSaver.setInstances(data);
        arffSaver.writeBatch();
    }

    @Override
    public void write(Instances data, OutputStream outputStream) throws Exception {
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setDestination(outputStream);
        arffSaver.setInstances(data);
        arffSaver.writeBatch();
    }
}
