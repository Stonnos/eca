package eca.data.file.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import weka.core.Instances;

import java.io.File;

/**
 * Implements saving data into json file.
 *
 * @author Roman Batygin
 */
public class JsonSaver extends AbstractDataSaver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    @Override
    public void write(Instances data) throws Exception {
        InstancesModel instances = INSTANCES_CONVERTER.convert(data);
        OBJECT_MAPPER.writeValue(getFile(), instances);
    }

    @Override
    protected void validateFile(File file) {
        super.validateFile(file);
        if (!file.getName().endsWith(DataFileExtension.JSON.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

}
