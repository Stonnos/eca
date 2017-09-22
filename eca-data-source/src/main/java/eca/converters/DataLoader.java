package eca.converters;

import org.springframework.util.Assert;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;

/**
 * Class for loading input data from file.
 *
 * @author Roman Batygin
 */

public class DataLoader {

    /**
     * Loads {@link Instances} from file.
     *
     * @param file file object
     * @return {@link Instances} object
     * @throws Exception
     */
    public static Instances getDataSet(File file) throws Exception {
        Assert.notNull(file, "File is not specified!");
        Instances data;
        if (file.getName().endsWith(DataFileExtension.CSV)
                || file.getName().endsWith(DataFileExtension.ARFF)) {
            ConverterUtils.DataSource source
                    = new ConverterUtils.DataSource(file.getAbsolutePath());
            data = source.getDataSet();
        } else if (file.getName().endsWith(DataFileExtension.XLS)
                || file.getName().endsWith(DataFileExtension.XLSX)) {
            XLSLoader loader = new XLSLoader();
            loader.setFile(file);
            data = loader.getDataSet();
        } else {
            throw new Exception(String.format("Can't load data from file '%s'", file.getAbsoluteFile()));
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}
