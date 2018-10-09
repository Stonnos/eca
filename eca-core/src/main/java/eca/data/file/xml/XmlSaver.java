package eca.data.file.xml;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import weka.core.Instances;
import weka.core.xml.XMLInstances;

import java.io.File;

/**
 * Implements saving data into xml file.
 */
public class XmlSaver extends AbstractDataSaver {


    @Override
    public void write(Instances data) throws Exception {
        XMLInstances xmlInstances = new XMLInstances();
        xmlInstances.setInstances(data);
        xmlInstances.write(getFile());
    }

    @Override
    protected void validateFile(File file) {
        super.validateFile(file);
        if (!file.getName().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

}
