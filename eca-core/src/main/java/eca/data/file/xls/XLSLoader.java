/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file.xls;

import eca.data.DataFileExtension;
import eca.data.file.FileDataDictionary;
import eca.data.file.resource.DataResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

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

    private static final Set<CellType> AVAILABLE_CELL_TYPES =
            EnumSet.of(CellType.STRING, CellType.NUMERIC, CellType.BLANK, CellType.BOOLEAN);

    private DataResource resource;

    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    /**
     * Returns data file resource.
     *
     * @return data file resource
     */
    public DataResource getResource() {
        return resource;
    }

    /**
     * Sets data file resource
     *
     * @param resource - data file resource
     */
    public void setResource(DataResource resource) {
        Objects.requireNonNull(resource, "Resource is not specified!");
        if (!resource.getFile().endsWith(DataFileExtension.XLS.getExtension()) &&
                !resource.getFile().endsWith(DataFileExtension.XLSX.getExtension())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
        this.resource = resource;
    }

    /**
     * Returns date format.
     *
     * @return date format
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * Sets date format.
     *
     * @param dateFormat date format
     */
    public void setDateFormat(String dateFormat) {
        Objects.requireNonNull(dateFormat, "Date format is not specified!");
        this.dateFormat = dateFormat;
    }

    /**
     * Reads data from xls/xlsx file.
     *
     * @return {@link Instances} object
     * @throws Exception
     */
    public Instances getDataSet() throws Exception {
        Instances data;
        try (Workbook book = WorkbookFactory.create(getResource().openInputStream())) {
            Sheet sheet = book.getSheetAt(0);
            validateData(sheet);
            data = new Instances(sheet.getSheetName(), createAttributes(sheet),
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
                            case NUMERIC:
                                if (data.attribute(j).isDate()) {
                                    o.setValue(j, cell.getDateCellValue().getTime());
                                } else {
                                    o.setValue(j, cell.getNumericCellValue());
                                }
                                break;

                            case STRING:
                                String stringValue = cell.getStringCellValue().trim();
                                if (StringUtils.isEmpty(stringValue)) {
                                    o.setValue(j, Utils.missingValue());
                                } else {
                                    o.setValue(j, stringValue);
                                }
                                break;

                            case BOOLEAN:
                                String val = String.valueOf(cell.getBooleanCellValue());
                                o.setValue(j, val);
                                break;

                            case BLANK:
                                o.setValue(j, Utils.missingValue());
                                break;

                            default:
                                throw new IllegalArgumentException(FileDataDictionary.BAD_CELL_VALUES);

                        }
                    }
                }
                data.add(o);
            }
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }

    private int getColNum(Sheet sheet) {
        return sheet.getRow(0).getPhysicalNumberOfCells();
    }

    private String getColName(Sheet sheet, int i) {
        return sheet.getRow(0).getCell(i).getStringCellValue().trim();
    }

    private ArrayList<Attribute> createAttributes(Sheet sheet) {
        ArrayList<Attribute> attr = new ArrayList<>();
        for (int i = 0; i < getColNum(sheet); i++) {
            ArrayList<String> values = new ArrayList<>();
            boolean isDate = false;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Cell cell = sheet.getRow(j).getCell(i);
                if (cell != null && !cell.getCellTypeEnum().equals(CellType.BLANK)) {
                    CellType cellType = cell.getCellTypeEnum();
                    if (cellType.equals(CellType.STRING)) {
                        String stringValue = cell.getStringCellValue().trim();
                        if (!StringUtils.isEmpty(stringValue) && !values.contains(stringValue)) {
                            values.add(stringValue);
                        }
                    } else if (cellType.equals(CellType.BOOLEAN)) {
                        String booleanValue = String.valueOf(cell.getBooleanCellValue());
                        if (!values.contains(booleanValue)) {
                            values.add(booleanValue);
                        }
                    } else if (cellType.equals(CellType.NUMERIC) && DateUtil.isCellDateFormatted(cell)) {
                        isDate = true;
                    }
                }
            }
            Attribute attribute;
            if (isDate) {
                attribute = new Attribute(getColName(sheet, i), dateFormat);
            } else if (values.isEmpty()) {
                attribute = new Attribute(getColName(sheet, i));
            } else {
                attribute = new Attribute(getColName(sheet, i), values);
            }
            attr.add(attribute);
        }
        return attr;
    }

    private void validateData(Sheet sheet) {
        int numHeaders = sheet.getRow(0).getLastCellNum();
        if (numHeaders >= sheet.getRow(0).getPhysicalNumberOfCells()) {
            throw new IllegalArgumentException(FileDataDictionary.EMPTY_COLUMNS_ERROR);
        }
        for (int i = 0; i < getColNum(sheet); i++) {
            CellType cellType = null;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    throw new IllegalArgumentException(FileDataDictionary.BAD_DATA_FORMAT);
                }
                if (row.getPhysicalNumberOfCells() > numHeaders) {
                    throw new IllegalArgumentException(FileDataDictionary.HEADER_ERROR);
                }
                Cell cell = row.getCell(i);
                if (cell != null) {
                    CellType cellTypeEnum = cell.getCellTypeEnum();
                    if (!AVAILABLE_CELL_TYPES.contains(cellTypeEnum)) {
                        throw new IllegalArgumentException(FileDataDictionary.BAD_CELL_VALUES);
                    }
                    if (isCellTypeEquals(cellType, cellTypeEnum)) {
                        throw new IllegalArgumentException(String.format(
                                FileDataDictionary.DIFFERENT_DATA_TYPES_IN_COLUMN_ERROR_FORMAT, i));
                    } else {
                        cellType = cell.getCellTypeEnum();
                    }
                }
            }
        }
    }

    private boolean isCellTypeEquals(CellType cellTypeA, CellType cellTypeB) {
        return cellTypeA != null && !cellTypeA.equals(CellType.BLANK) && !cellTypeB.equals(CellType.BLANK) && !cellTypeA.equals(cellTypeB);
    }
}
