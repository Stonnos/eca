package eca.data.file.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.file.converter.InstancesConverter;
import eca.data.file.model.InstancesModel;
import weka.core.Instances;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Implements saving data into json file.
 *
 * @author Roman Batygin
 */
public class JsonSaver extends AbstractDataSaver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final InstancesConverter INSTANCES_CONVERTER = new InstancesConverter();

    public JsonSaver() {
        super(Collections.singleton(DataFileExtension.JSON.getExtendedExtension()));
    }

    @Override
    protected void internalWrite(Instances data, File file) throws Exception {
        InstancesModel instances = INSTANCES_CONVERTER.convert(data);
        OBJECT_MAPPER.writeValue(file, instances);
    }

    @Override
    public void write(Instances data, OutputStream outputStream) throws Exception {
        InstancesModel instances = INSTANCES_CONVERTER.convert(data);
        OBJECT_MAPPER.writeValue(outputStream, instances);
    }
}
