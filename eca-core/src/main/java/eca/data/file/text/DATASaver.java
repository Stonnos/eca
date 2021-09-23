package eca.data.file.text;

import eca.data.AbstractDataSaver;
import eca.data.FileUtils;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static eca.util.Utils.getAttributesAsString;

/**
 * Implements saving data into text file.
 *
 * @author Roman Batygin
 */
public class DATASaver extends AbstractDataSaver {

    public DATASaver() {
        super(FileUtils.TXT_EXTENSIONS);
    }

    @Override
    protected void internalWrite(Instances data, File file) throws IOException {
        List<String> rows = createRows(data);
        org.apache.commons.io.FileUtils.writeLines(file, StandardCharsets.UTF_8.name(), rows);
    }

    private List<String> createRows(Instances data) {
        List<String> rows = new ArrayList<>(data.numInstances());
        rows.add(getAttributesAsString(data));
        data.forEach(instance -> rows.add(instance.toString()));
        return rows;
    }
}
