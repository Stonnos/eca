package eca.data.file.text;

import eca.data.FileUtils;
import eca.data.file.resource.DataResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements training data loading from file with extensions such as:
 * - txt
 * - data
 *
 * @author Roman Batygin
 */
public class DATALoader extends AbstractTextLoader {

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!FileUtils.isTxtExtension(resource.getFile())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    @Override
    protected List<List<String>> readDataFromResource() throws IOException {
        List<List<String>> data = new ArrayList<>();
        String line;
        int columnSize = 0;
        int rowIdx = 1;
        try (InputStream inputStream = getSource().openInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            while ((line = reader.readLine()) != null) {
                List<String> row = parseLine(line, columnSize, rowIdx);
                data.add(row);
                columnSize = row.size();
                rowIdx++;
            }
        }
        return data;
    }
}
