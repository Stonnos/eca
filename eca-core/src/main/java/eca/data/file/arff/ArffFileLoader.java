package eca.data.file.arff;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.resource.DataResource;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

import java.io.InputStream;

/**
 * Class for loading instances from arff files.
 *
 * @author Roman Batygin
 */
public class ArffFileLoader extends AbstractDataLoader<DataResource> {

    @Override
    public Instances loadInstances() throws Exception {
        try (InputStream inputStream = getSource().openInputStream()) {
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setSource(inputStream);
            return arffLoader.getDataSet();
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.ARFF.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

}
