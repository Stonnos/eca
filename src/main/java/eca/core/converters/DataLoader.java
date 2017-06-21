package eca.core.converters;

import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;

/**
 * @author Roman Batygin
 */

public class DataLoader {

    private static final String[] FILE_EXTENSIONS = {".xls", ".xlsx", ".csv", ".arff"};

    public static Instances getDataSet(File file) throws Exception {
        Instances data;
        if (file.getName().endsWith(FILE_EXTENSIONS[2]) || file.getName().endsWith(FILE_EXTENSIONS[3])) {
            ConverterUtils.DataSource source
                    = new ConverterUtils.DataSource(file.getAbsolutePath());
            data = source.getDataSet();
        } else if (file.getName().endsWith(FILE_EXTENSIONS[0]) || file.getName().endsWith(FILE_EXTENSIONS[1])) {
            XLSLoader loader = new XLSLoader();
            loader.setFile(file);
            data = loader.getDataSet();
        } else {
            throw new Exception("Wrong file extension!");
        }
        data.setClassIndex(data.numAttributes() - 1);
        return data;
    }
}
