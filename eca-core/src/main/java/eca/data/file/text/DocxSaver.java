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

import static eca.util.Utils.getAttributesAsString;

/**
 * Implements saving data into docx file.
 *
 * @author Roman Batygin
 */
public class DocxSaver extends AbstractDataSaver {

    public DocxSaver() {
        super(FileUtils.DOCX_EXTENSIONS);
    }

    @Override
    protected void internalWrite(Instances data, File file) throws IOException {
        try (XWPFDocument document = new XWPFDocument(); FileOutputStream out = new FileOutputStream(file)) {
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(getAttributesAsString(data));
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
}
