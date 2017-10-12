package eca.data.file;

import eca.data.AbstractDataLoader;
import eca.data.FileExtension;
import org.springframework.util.Assert;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */
public class FileDataLoader extends AbstractDataLoader {

    private File file;

    /**
     * Sets the file object.
     *
     * @param file file object
     * @throws Exception if a file object is null or has invalid extension
     */
    public void setFile(File file) throws Exception {
        Assert.notNull(file, "File is not specified!");
        if (!file.getName().endsWith(FileExtension.XLS) && !file.getName().endsWith(FileExtension.XLSX)) {
            throw new Exception("Wrong file extension!");
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
        if (file.getName().endsWith(FileExtension.CSV)
                || file.getName().endsWith(FileExtension.ARFF)) {
            ConverterUtils.DataSource source
                    = new ConverterUtils.DataSource(file.getAbsolutePath());
            data = source.getDataSet();
        } else if (file.getName().endsWith(FileExtension.XLS)
                || file.getName().endsWith(FileExtension.XLSX)) {
            XLSLoader loader = new XLSLoader();
            loader.setFile(file);
            loader.setDateFormat(getDateFormat());
            data = loader.getDataSet();
        } else {
            throw new Exception(String.format("Can't load data from file '%s'", file.getAbsoluteFile()));
        }
        if (data == null) {
            throw new Exception(String.format("Can't load data from file '%s'. Data is null!",
                    file.getAbsoluteFile()));
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}
