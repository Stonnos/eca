package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.DataFileExtension;
import eca.data.FileUtil;
import eca.data.net.UrlDataLoaderDictionary;
import eca.util.Utils;
import org.springframework.util.Assert;
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
public class FileDataLoader extends AbstractDataLoader {

    private static final String[] FILE_EXTENSIONS = DataFileExtension.getExtensions();

    private File file;

    /**
     * Sets the file object.
     *
     * @param file file object
     * @throws IllegalArgumentException if a file object is null or has invalid extension
     */
    public void setFile(File file) {
        Assert.notNull(file, "File is not specified!");
        if (!Utils.contains(FILE_EXTENSIONS, file.getName(), (x, y) -> x.endsWith(y))) {
            throw new IllegalArgumentException(String.format(UrlDataLoaderDictionary.BAD_FILE_EXTENSION_ERROR_FORMAT,
                    Arrays.asList(FILE_EXTENSIONS)));
        }
        this.file = file;
    }

    /**
     * Returns file object.
     *
     * @return file object
     */
    public File getFile() {
        return file;
    }

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        if (FileUtil.isWekaExtension(file.getName())) {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource(file.getAbsolutePath());
            data = source.getDataSet();
        } else if (FileUtil.isXlsExtension(file.getName())) {
            XLSLoader loader = new XLSLoader();
            loader.setFile(file);
            loader.setDateFormat(getDateFormat());
            data = loader.getDataSet();
        } else {
            throw new Exception(String.format("Can't load data from file '%s'", file.getAbsoluteFile()));
        }
        if (Objects.isNull(data)) {
            throw new Exception(String.format("Can't load data from file '%s'. Data is null!", file.getAbsoluteFile()));
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}
