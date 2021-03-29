package eca.data.file.csv;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.file.resource.DataResource;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.InputStream;

/**
 * Class for loading instances from csv files.
 *
 * @author Roman Batygin
 */
public class CsvLoader extends AbstractDataLoader<DataResource> {

    @Override
    public Instances loadInstances() throws Exception {
        try (InputStream inputStream = getSource().openInputStream()) {
            CSVLoader csvLoader = new CSVLoader();
            csvLoader.setSource(inputStream);
            return csvLoader.getDataSet();
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!resource.getFile().endsWith(DataFileExtension.CSV.getExtendedExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

}
