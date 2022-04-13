package eca.data.file.text;

import eca.data.AbstractDataSaver;
import eca.data.FileUtils;
import org.apache.commons.io.IOUtils;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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

    @Override
    public void write(Instances data, OutputStream outputStream) throws Exception {
        List<String> rows = createRows(data);
        IOUtils.writeLines(rows, null, outputStream, StandardCharsets.UTF_8.name());
    }

    private List<String> createRows(Instances data) {
        List<String> rows = new ArrayList<>(data.numInstances());
        rows.add(getAttributesAsString(data));
        data.forEach(instance -> rows.add(instance.toString()));
        return rows;
    }
}
