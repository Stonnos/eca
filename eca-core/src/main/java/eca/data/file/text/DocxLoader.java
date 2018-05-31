package eca.data.file.text;

import eca.data.FileUtils;
import eca.data.file.resource.DataResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements training data loading from file with docx extension.
 *
 * @author Roman Batygin
 */
public class DocxLoader extends AbstractTextLoader {

    private static final String ROWS_SPLIT_REGEX = "\n";

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!FileUtils.isDocxExtension(resource.getFile())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    @Override
    protected List<List<String>> readDataFromResource() throws IOException {
        List<List<String>> data;
        int columnSize = 0;
        int rowIdx = 1;
        try (XWPFDocument document = new XWPFDocument(getSource().openInputStream());
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            String fileData = extractor.getText();
            if (StringUtils.isEmpty(fileData)) {
                throw new IllegalArgumentException(String.format("File '%s' has empty data!", getSource().getFile()));
            }
            String[] lines = fileData.split(ROWS_SPLIT_REGEX);
            data = new ArrayList<>(lines.length);
            for (String line : lines) {
                List<String> row = parseLine(line, columnSize, rowIdx);
                data.add(row);
                columnSize = row.size();
                rowIdx++;
            }
        }
        return data;
    }
}
