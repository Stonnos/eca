/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.converters;

import eca.gui.text.DateFormat;
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
 *
 * @author Рома
 */
public class XLSSaver {
    
    private File file;
    
    public void setFile(File file) throws IOException {
        if (!file.getName().endsWith(".xls") && !file.getName().endsWith(".xlsx")) {
            throw new IOException("Wrong file extension!");
        }
        file.createNewFile();
        this.file = file;
    }
    
    public void write(Instances data) throws IOException {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            Workbook book = file.getName().endsWith(".xls") ?
                    new HSSFWorkbook() : new XSSFWorkbook();
            Font font = book.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short)12);
            CellStyle style = book.createCellStyle();
            CellStyle dateStyle = book.createCellStyle();
            short date = book.createDataFormat().getFormat(DateFormat.DATE_FORMAT);
            dateStyle.setDataFormat(date);
            style.setFont(font);
            Sheet sheet = book.createSheet(data.relationName());    
            Row row = sheet.createRow(sheet.getPhysicalNumberOfRows());
            //----------------------------------------
            for (int i = 0; i < data.numAttributes(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellStyle(style);
                cell.setCellValue(data.attribute(i).name());
            }
            //----------------------------------------
            for (int i = 0; i < data.numInstances(); i++) {
                row = sheet.createRow(sheet.getPhysicalNumberOfRows());
                for (int j = 0; j < data.numAttributes(); j++) {
                    Cell cell = row.createCell(j);
                    Attribute a = data.attribute(j);
                    if (!data.instance(i).isMissing(a)) {
                        if (a.isDate()) {
                            cell.setCellStyle(dateStyle);
                            cell.setCellValue(new Date((long)data.instance(i).value(a)));
                        }
                        else if (a.isNumeric()) {
                            cell.setCellValue(data.instance(i).value(a));
                        } else cell.setCellValue(data.instance(i).stringValue(a));
                    }
                }
            }
            //----------------------------
            book.write(stream);
        }
        catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
        
    }
    
}
