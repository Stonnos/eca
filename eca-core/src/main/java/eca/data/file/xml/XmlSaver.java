package eca.data.file.xml;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import weka.core.Instances;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.Collections;

/**
 * Implements saving data into xml file.
 *
 * @author Roman Batygin
 */
public class XmlSaver extends AbstractDataSaver {

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    public XmlSaver() {
        super(Collections.singleton(DataFileExtension.XML.getExtendedExtension()));
    }

    @Override
    protected void internalWrite(Instances data, File file) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(InstancesModel.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(INSTANCES_CONVERTER.convert(data), file);
    }

}
