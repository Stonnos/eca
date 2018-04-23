package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import eca.data.file.resource.FileResource;
import eca.data.net.UrlDataLoaderDictionary;
import eca.util.Utils;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */
public class FileDataLoader extends AbstractDataLoader<File> {

    private static final String[] FILE_EXTENSIONS = DataFileExtension.getExtensions();

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        if (FileUtils.isWekaExtension(getSource().getName())) {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(getSource().getAbsolutePath());
            data = source.getDataSet();
        } else if (FileUtils.isXlsExtension(getSource().getName())) {
            XLSLoader loader = new XLSLoader();
            loader.setResource(new FileResource(getSource()));
            loader.setDateFormat(getDateFormat());
            data = loader.getDataSet();
        } else {
            throw new IllegalArgumentException(
                    String.format("Can't load data from file '%s'", getSource().getAbsoluteFile()));
        }
        if (Objects.isNull(data)) {
            throw new IllegalArgumentException(
                    String.format("Can't load data from file '%s'. Data is null!", getSource().getAbsoluteFile()));
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }

    @Override
    protected void validateSource(File file) {
        super.validateSource(file);
        if (!Utils.contains(FILE_EXTENSIONS, file.getName(), (x, y) -> x.endsWith(y))) {
            throw new IllegalArgumentException(String.format(UrlDataLoaderDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
    }
}
