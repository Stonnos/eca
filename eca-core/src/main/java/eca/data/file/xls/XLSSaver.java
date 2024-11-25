package eca.data.file.xls;

import eca.data.AbstractDataSaver;
import eca.data.FileUtils;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Implements saving {@link Instances} into XLS file.
 *
 * @author Roman Batygin
 */
public class XLSSaver extends AbstractDataSaver {

    private static final short FONT_SIZE = 12;

    private static final String EXCEL_DATE_FORMAT = "dd-mm-yyyy h:mm:ss";
    private static final String APPLICATION_VERSION = "1.0";

    public XLSSaver() {
        super(FileUtils.XLS_EXTENSIONS);
    }

    @Override
    protected void internalWrite(Instances data, File file) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             Workbook book = createWorkbook(fileOutputStream, data);
             Worksheet sheet = book.newWorksheet(data.relationName())) {
            writeWorkbook(sheet, data);
            book.finish();
        }
    }

    @Override
    public void write(Instances data, OutputStream outputStream) throws Exception {
        try (Workbook book = new Workbook(outputStream, data.relationName(), "1.0");
             Worksheet sheet = book.newWorksheet(data.relationName())) {
            writeWorkbook(sheet, data);
            book.finish();
        }
    }

    private void writeWorkbook(Worksheet sheet, Instances data) {
        fillHeaderCells(data, sheet);
        fillDataCells(data, sheet);
    }

    private void fillHeaderCells(Instances data, Worksheet sheet) {
        for (int i = 0; i < data.numAttributes(); i++) {
            sheet.style(0, i).bold().fontSize(FONT_SIZE).set();
            sheet.value(0, i, data.attribute(i).name());
        }
    }

    private void fillDataCells(Instances data, Worksheet sheet) {
        for (int i = 0; i < data.numInstances(); i++) {
            for (int j = 0; j < data.numAttributes(); j++) {
                Attribute a = data.attribute(j);
                if (!data.instance(i).isMissing(a)) {
                    if (a.isDate()) {
                        sheet.style(i + 1, j).format(EXCEL_DATE_FORMAT).set();
                        sheet.value(i + 1, j, new Date((long) data.instance(i).value(a)));
                    } else if (a.isNumeric()) {
                        sheet.value(i + 1, j, data.instance(i).value(a));
                    } else {
                        sheet.value(i + 1, j, data.instance(i).stringValue(a));
                    }
                }
            }
        }
    }

    private Workbook createWorkbook(OutputStream outputStream, Instances data) {
        return new Workbook(outputStream, data.relationName(), APPLICATION_VERSION);
    }
}
