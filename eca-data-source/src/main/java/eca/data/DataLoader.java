package eca.data;

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

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns date format.
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Assert.notNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Loads {@link Instances} from file.
     *
     * @param file file object
     * @return {@link Instances} object
     * @throws Exception
     */
    public Instances getDataSet(File file) throws Exception {
        Assert.notNull(file, "File is not specified!");
        Instances data;
        if (file.getName().endsWith(DataFileExtension.CSV)
                || file.getName().endsWith(DataFileExtension.ARFF)) {
            ConverterUtils.DataSource source
                    = new ConverterUtils.DataSource(file.getAbsolutePath());
            data = source.getDataSet();
            if (data == null) {
                throw new Exception(String.format("Can't load data from file '%s'. Data is null!",
                        file.getAbsoluteFile()));
            }
        } else if (file.getName().endsWith(DataFileExtension.XLS)
                || file.getName().endsWith(DataFileExtension.XLSX)) {
            XLSLoader loader = new XLSLoader();
            loader.setFile(file);
            loader.setDateFormat(dateFormat);
            data = loader.getDataSet();
        } else {
            throw new Exception(String.format("Can't load data from file '%s'", file.getAbsoluteFile()));
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}
