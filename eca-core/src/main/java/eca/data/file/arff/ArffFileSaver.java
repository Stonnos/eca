package eca.data.file.arff;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;

/**
 * Implements saving data into arff file.
 *
 * @author Roman Batygin
 */
public class ArffFileSaver extends AbstractDataSaver {

    @Override
    public void write(Instances data) throws Exception {
        ArffSaver arffSaver = new ArffSaver();
        arffSaver.setFile(getFile());
        arffSaver.setInstances(data);
        arffSaver.writeBatch();
    }

    @Override
    protected boolean isValidFile(File file) {
        return file.getName().endsWith(DataFileExtension.ARFF.getExtendedExtension());
    }

}
