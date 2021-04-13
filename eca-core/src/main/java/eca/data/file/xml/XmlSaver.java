package eca.data.file.xml;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import weka.core.Instances;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * Implements saving data into xml file.
 *
 * @author Roman Batygin
 */
public class XmlSaver extends AbstractDataSaver {

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    @Override
    public void write(Instances data) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(InstancesModel.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(INSTANCES_CONVERTER.convert(data), getFile());
    }

    @Override
    protected boolean isValidFile(File file) {
        return file.getName().endsWith(DataFileExtension.XML.getExtendedExtension());
    }

}
