package eca.data.file.text;

import eca.data.AbstractDataSaver;
import eca.data.FileUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implements saving data into docx file.
 *
 * @author Roman Batygin
 */
public class DocxSaver extends AbstractDataSaver {

    private static final String HEADER_FORMAT = ",%s";

    @Override
    public void write(Instances data) throws IOException {
        try (XWPFDocument document = new XWPFDocument(); FileOutputStream out = new FileOutputStream(getFile())) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(createHeader(data));
            if (!data.isEmpty()) {
                run.addBreak();
                for (int i = 0; i < data.numInstances() - 1; i++) {
                    run.setText(data.instance(i).toString());
                    run.addBreak();
                }
                run.setText(data.lastInstance().toString());
            }
            document.write(out);
        }
    }

    @Override
    protected void validateFile(File file) {
        super.validateFile(file);
        if (!FileUtils.isDocxExtension(file.getName())) {
            throw new IllegalArgumentException(String.format("Unexpected extension for file: %s!", file.getName()));
        }
    }

    private String createHeader(Instances data) {
        StringBuilder header = new StringBuilder(data.attribute(0).name());
        for (int i = 1; i < data.numAttributes(); i++) {
            header.append(String.format(HEADER_FORMAT, data.attribute(i).name()));
        }
        return header.toString();
    }
}
