package eca.data.file.xml;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import eca.data.file.resource.DataResource;
import weka.core.Instances;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
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

    @Override
    public Instances loadInstances() throws Exception {
        try (InputStream inputStream = getSource().openInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            JAXBContext jaxbContext = JAXBContext.newInstance(InstancesModel.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            InstancesModel instancesModel = (InstancesModel) unmarshaller.unmarshal(inputStreamReader);
            Instances instances = INSTANCES_CONVERTER.convert(instancesModel);
            instances.setClassIndex(instances.numAttributes() - 1);
            return instances;
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.XML.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

}
