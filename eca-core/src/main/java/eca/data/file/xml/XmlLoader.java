package eca.data.file.xml;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import eca.data.file.resource.DataResource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class for loading xml files.
 *
 * @author Roman Batygin
 */
public class XmlLoader extends AbstractDataLoader<DataResource> {

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    private Unmarshaller unmarshaller;

    @Override
    public Instances loadInstances() throws Exception {
        try (InputStream inputStream = getSource().openInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            Unmarshaller unmarshaller = getOrCreateUnmarshaller();
            InstancesModel instancesModel = (InstancesModel) unmarshaller.unmarshal(reader);
            return INSTANCES_CONVERTER.convert(instancesModel);
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    private synchronized Unmarshaller getOrCreateUnmarshaller() throws JAXBException {
        if (unmarshaller == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(InstancesModel.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        }
        return unmarshaller;
    }

}
