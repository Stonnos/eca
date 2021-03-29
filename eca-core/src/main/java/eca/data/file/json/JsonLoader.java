package eca.data.file.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import eca.data.file.resource.DataResource;
import weka.core.Instances;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Class for loading instances from json files.
 *
 * @author Roman Batygin
 */
public class JsonLoader extends AbstractDataLoader<DataResource> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    @Override
    public Instances loadInstances() throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getSource().openInputStream(), StandardCharsets.UTF_8))) {
            InstancesModel instancesModel =  OBJECT_MAPPER.readValue(reader, InstancesModel.class);
            Instances instances = INSTANCES_CONVERTER.convert(instancesModel);
            instances.setClassIndex(instances.numAttributes() - 1);
            return instances;
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.JSON.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

}
