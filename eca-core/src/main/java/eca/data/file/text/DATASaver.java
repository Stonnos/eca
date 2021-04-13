package eca.data.file.text;

import eca.data.AbstractDataSaver;
import eca.data.FileUtils;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements saving data into text file.
 *
 * @author Roman Batygin
 */
public class DATASaver extends AbstractDataSaver {

    private static final String HEADER_FORMAT = ",%s";

    @Override
    public void write(Instances data) throws IOException {
        List<String> rows = createRows(data);
        org.apache.commons.io.FileUtils.writeLines(getFile(), StandardCharsets.UTF_8.name(), rows);
    }

    @Override
    protected boolean isValidFile(File file) {
        return FileUtils.isTxtExtension(file.getName());
    }

    private List<String> createRows(Instances data) {
        List<String> rows = new ArrayList<>(data.numInstances());
        rows.add(createHeader(data));
        data.forEach(instance -> rows.add(instance.toString()));
        return rows;
    }

    private String createHeader(Instances data) {
        StringBuilder header = new StringBuilder(data.attribute(0).name());
        for (int i = 1; i < data.numAttributes(); i++) {
            header.append(String.format(HEADER_FORMAT, data.attribute(i).name()));
        }
        return header.toString();
    }
}
