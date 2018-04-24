package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import eca.data.file.resource.DataResource;
import eca.data.file.xls.XLSLoader;
import eca.util.Utils;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */
public class FileDataLoader extends AbstractDataLoader<DataResource> {

    private static final String[] FILE_EXTENSIONS = DataFileExtension.getExtensions();
    private static final String FILE_EXTENSION_FORMAT = ".%s";

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        try (InputStream inputStream = getSource().openInputStream()) {
            if (FileUtils.isWekaExtension(getSource().getFile())) {
                ConverterUtils.DataSource source = new ConverterUtils.DataSource(inputStream);
                data = source.getDataSet();
                if (Objects.isNull(data)) {
                    throw new IllegalArgumentException(
                            String.format("Can't load data from file '%s'. Data is null!", getSource().getFile()));
                }
            } else if (FileUtils.isXlsExtension(getSource().getFile())) {
                XLSLoader loader = new XLSLoader();
                loader.setResource(getSource());
                loader.setDateFormat(getDateFormat());
                data = loader.getDataSet();
            } else {
                throw new IllegalArgumentException(
                        String.format("Can't load data from file '%s'", getSource().getFile()));
            }
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    @Override
    protected void validateSource(DataResource dataResource) {
        super.validateSource(dataResource);
        if (!Utils.contains(FILE_EXTENSIONS, dataResource.getFile(),
                (x, y) -> x.endsWith(String.format(FILE_EXTENSION_FORMAT, y)))) {
            throw new IllegalArgumentException(String.format(FileDataDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
    }
}
