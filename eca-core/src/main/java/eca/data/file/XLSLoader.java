/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file;

import eca.data.FileExtension;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.util.Assert;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Class for loading xls/xlsx files. <p>
 * Input data must satisfy to following requirements: <p>
 * - Cells of the following formats are allowed: numeric formats, date, text, boolean; <p>
 * - Data should not contain extraneous records; <p>
 * - Data can not contain empty columns; <p>
 * - Each column must contain data of the same type. <p>
 *
 * @author Roman Batygin
 */
public class XLSLoader {

    private InputStream inputStream;

    private File file;

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Sets the file object.
     *
     * @param file file object
     * @throws Exception if a file object is null or has invalid extension
     */
    public void setFile(File file) throws Exception {
        Assert.notNull(file, "File is not specified!");
        if (!file.getName().endsWith(FileExtension.XLS) && !file.getName().endsWith(FileExtension.XLSX)) {
            throw new Exception("Wrong file extension!");
        }
        this.file = file;
    }

    /**
     * Returns file object.
     *
     * @return file object
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets <tt>InputStream</tt> object.
     *
     * @param inputStream <tt>InputStream</tt> object
     */
    public void setInputStream(InputStream inputStream) {
        Assert.notNull(inputStream, "InputStream is not specified!");
        this.inputStream = inputStream;
    }

    /**
     * Returns <tt>InputStream</tt> object.
     *
     * @return <tt>InputStream</tt> object
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Returns date format.
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Assert.notNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Reads data from xls/xlsx file.
     *
     * @return <tt>Instances</tt> object
     * @throws Exception
     */
    public Instances getDataSet() throws Exception {
        Instances data;
        Workbook book = WorkbookFactory.create(inputStream == null ?
                new FileInputStream(file) : inputStream);
        Sheet sheet = book.getSheetAt(0);
        checkData(sheet);
        data = new Instances(sheet.getSheetName(), makeAttributes(sheet),
                sheet.getLastRowNum());

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            DenseInstance o = new DenseInstance(data.numAttributes());
            o.setDataset(data);
            for (int j = 0; j < data.numAttributes(); j++) {
                Cell cell = sheet.getRow(i).getCell(j);
                if (cell == null) {
                    o.setValue(j, Utils.missingValue());
                } else {
                    switch (cell.getCellTypeEnum()) {
                        case NUMERIC: {
                            if (data.attribute(j).isDate()) {
                                o.setValue(j, cell.getDateCellValue().getTime());
                            } else {
                                o.setValue(j, cell.getNumericCellValue());
                            }
                            break;
                        }

                        case STRING: {
                            String val = cell.getStringCellValue().trim();
                            if (val.isEmpty()) {
                                o.setValue(j, Utils.missingValue());
                            } else {
                                o.setValue(j, val);
                            }
                            break;
                        }

                        case BOOLEAN: {
                            String val = String.valueOf(cell.getBooleanCellValue());
                            o.setValue(j, val);
                            break;
                        }
                    }
                }
            }
            data.add(o);
        }
        return data;
    }

    private int getColNum(Sheet sheet) {
        return sheet.getRow(0).getPhysicalNumberOfCells();
    }

    private String getColName(Sheet sheet, int i) {
        return sheet.getRow(0).getCell(i).getStringCellValue().trim();
    }

    private ArrayList<Attribute> makeAttributes(Sheet sheet) throws Exception {
        ArrayList<Attribute> attr = new ArrayList<>();
        for (int i = 0; i < getColNum(sheet); i++) {
            ArrayList<String> values = new ArrayList<>();
            boolean isDate = false;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Cell cell = sheet.getRow(j).getCell(i);
                if (cell != null && !cell.getCellTypeEnum().equals(CellType.BLANK)) {
                    CellType cellType = cell.getCellTypeEnum();
                    if (cellType.equals(CellType.STRING)) {
                        String val = cell.getStringCellValue().trim();
                        if (!val.isEmpty() && !values.contains(val)) {
                            values.add(val);
                        }
                    } else if (cellType.equals(CellType.BOOLEAN)) {
                        String val = String.valueOf(cell.getBooleanCellValue());
                        if (!values.contains(val)) {
                            values.add(val);
                        }
                    } else if (cellType.equals(CellType.NUMERIC)
                            && DateUtil.isCellDateFormatted(cell)) {
                        isDate = true;
                    }
                }
            }
            Attribute a;
            if (isDate) {
                a = new Attribute(getColName(sheet, i), dateFormat);
            } else if (values.isEmpty()) {
                a = new Attribute(getColName(sheet, i));
            } else {
                a = new Attribute(getColName(sheet, i), values);
            }
            attr.add(a);
        }
        return attr;
    }

    private void checkData(Sheet sheet) throws Exception {

        if (sheet.getRow(0).getLastCellNum() > sheet.getRow(0).getPhysicalNumberOfCells()) {
            throw new Exception(FileDataDictionary.EMPTY_COLUMNS_ERROR);
        }

        for (int i = 0; i < getColNum(sheet); i++) {
            CellType cellType = null;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    throw new Exception(FileDataDictionary.BAD_DATA_FORMAT);
                }
                Cell cell = sheet.getRow(j).getCell(i);
                if (cell != null) {
                    if (cell.getCellTypeEnum() != CellType.STRING
                            && cell.getCellTypeEnum() != CellType.NUMERIC
                            && cell.getCellTypeEnum() != CellType.BLANK
                            && cell.getCellTypeEnum() != CellType.BOOLEAN) {
                        throw new Exception(FileDataDictionary.BAD_CELL_VALUES);
                    }
                    CellType t = cell.getCellTypeEnum();
                    if (cellType != null && !t.equals(CellType.BLANK) && !t.equals(cellType)) {
                        throw new Exception(String.format(
                                FileDataDictionary.DIFFERENT_DATA_TYPES_IN_COLUMN_ERROR_FORMAT, i));
                    } else {
                        cellType = cell.getCellTypeEnum();
                    }

                }
            }
        }

    }
}
