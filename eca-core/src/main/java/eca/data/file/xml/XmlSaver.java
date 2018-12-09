package eca.data.file.xml;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.xml.converter.XmlInstancesConverter;
import eca.data.file.xml.model.XmlInstances;
import weka.core.Instances;
import weka.core.xml.XMLInstances;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Implements saving data into xml file.
 */
public class XmlSaver extends AbstractDataSaver {


    @Override
    public void write(Instances data) throws Exception {
       // XMLInstances xmlInstances = new XMLInstances();
       // xmlInstances.setInstances(data);
      //  xmlInstances.write(getFile());
        JAXBContext jaxbContext = JAXBContext.newInstance(XmlInstances.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        XmlInstancesConverter xmlInstancesConverter = new XmlInstancesConverter();
        marshaller.marshal(xmlInstancesConverter.convert(data), getFile());
    }

    @Override
    protected void validateFile(File file) {
        super.validateFile(file);
        if (!file.getName().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

}
