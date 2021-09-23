/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file.xls;

import eca.data.AbstractDataSaver;
import eca.data.DataFileExtension;
import eca.data.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import weka.core.Attribute;
import weka.core.Instances;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Implements saving {@link Instances} into XLS file.
 *
 * @author Roman Batygin
 */
public class XLSSaver extends AbstractDataSaver {

    private static final short FONT_SIZE = 12;

    public XLSSaver() {
        super(FileUtils.XLS_EXTENSIONS);
    }

    @Override
    protected void internalWrite(Instances data, File file) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file); Workbook book = createWorkbook(file)) {
            Sheet sheet = book.createSheet(data.relationName());
            fillHeaderCells(data, sheet.createRow(sheet.getPhysicalNumberOfRows()), createCellStyle(book));
            fillDataCells(data, book, sheet);
            book.write(stream);
        }
    }

    private CellStyle createCellStyle(Workbook book) {
        Font font = book.createFont();
        font.setBold(true);
        font.setFontHeightInPoints(FONT_SIZE);
        CellStyle style = book.createCellStyle();
        style.setFont(font);
        return style;
    }

    private void fillHeaderCells(Instances data, Row row, CellStyle style) {
        for (int i = 0; i < data.numAttributes(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(data.attribute(i).name());
        }
    }

    private void fillDataCells(Instances data, Workbook book, Sheet sheet) {
        CellStyle dateStyle = book.createCellStyle();
        short date = book.createDataFormat().getFormat(getDateFormat());
        dateStyle.setDataFormat(date);
        for (int i = 0; i < data.numInstances(); i++) {
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            for (int j = 0; j < data.numAttributes(); j++) {
                Cell cell = row.createCell(j);
                Attribute a = data.attribute(j);
                if (!data.instance(i).isMissing(a)) {
                    if (a.isDate()) {
                        cell.setCellStyle(dateStyle);
                        cell.setCellValue(new Date((long) data.instance(i).value(a)));
                    } else if (a.isNumeric()) {
                        cell.setCellValue(data.instance(i).value(a));
                    } else {
                        cell.setCellValue(data.instance(i).stringValue(a));
                    }
                }
            }
        }
    }

    private Workbook createWorkbook(File file) {
        return file.getName().endsWith(DataFileExtension.XLS.getExtendedExtension()) ? new HSSFWorkbook() :
                new XSSFWorkbook();
    }

}
