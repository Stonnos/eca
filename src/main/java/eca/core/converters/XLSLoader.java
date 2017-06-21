/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.converters;

import java.io.*;

import eca.gui.text.DateFormat;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.DenseInstance;
import org.apache.poi.ss.usermodel.*;
import java.util.ArrayList;
import weka.core.Utils;

/**
 *
 * @author Рома
 */
public class XLSLoader {

    private InputStream inputStream;
    private File file;

    public void setFile(File file) throws Exception {
        if (file == null) {
            throw new NullPointerException();
        }
        if (!file.getName().endsWith(".xls") && !file.getName().endsWith(".xlsx")) {
            throw new Exception("Wrong file extension!");
        }
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException();
        }
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Instances getDataSet() throws Exception {
        Instances data;
        try {
            Workbook book = WorkbookFactory.create(inputStream == null ?
                    new FileInputStream(file) : inputStream);
            Sheet sheet = book.getSheetAt(0);
            checkData(sheet);
            //-----------------------------
            data = new Instances(sheet.getSheetName(), makeAttributes(sheet),
                    sheet.getLastRowNum());
            //------------------------------
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
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
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
                    }
                    else if (cellType.equals(CellType.BOOLEAN)) {
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
                a = new Attribute(getColName(sheet, i), DateFormat.DATE_FORMAT);
            }
            else if (values.isEmpty()) {
                a = new Attribute(getColName(sheet, i));
            }
            else {
                a = new Attribute(getColName(sheet, i), values);
            }
            attr.add(a);
        }
        return attr;
    }

    private void checkData(Sheet sheet) throws Exception {

        if (sheet.getRow(0).getLastCellNum() > sheet.getRow(0).getPhysicalNumberOfCells()) {
            throw new Exception("Данные должны быть без пустых столбцов!");
        }

        for (int i = 0; i < getColNum(sheet); i++) {
            CellType cellType = null;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    throw new Exception("Данные не должны содержать посторонние записи!");
                }
                Cell cell = sheet.getRow(j).getCell(i);
                if (cell != null) {
                    if (cell.getCellTypeEnum() != CellType.STRING
                            && cell.getCellTypeEnum() != CellType.NUMERIC
                            && cell.getCellTypeEnum() != CellType.BLANK
                            && cell.getCellTypeEnum() != CellType.BOOLEAN) {
                        throw new Exception("Значения должны быть числовыми или текстовыми!");
                    }
                    CellType t = cell.getCellTypeEnum();
                    if (cellType != null  && !t.equals(CellType.BLANK) && !t.equals(cellType)) {
                        throw new Exception("Столбец " + i + " содержит данные различных типов!");
                    }
                    else {
                        cellType = cell.getCellTypeEnum();
                    }

                }
            }
        }

    }
}
