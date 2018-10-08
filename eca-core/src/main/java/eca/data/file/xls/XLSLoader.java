/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.data.file.xls;

import eca.data.AbstractDataLoader;
import eca.data.FileUtils;
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
public class XLSLoader extends AbstractDataLoader<DataResource> {

    private static final Set<CellType> AVAILABLE_CELL_TYPES =
            EnumSet.of(CellType.STRING, CellType.NUMERIC, CellType.BLANK, CellType.BOOLEAN);
    private static final int HEADER_INDEX = 0;

    @Override
    public Instances loadInstances() throws Exception {
        Instances data;
        try (Workbook book = WorkbookFactory.create(getSource().openInputStream())) {
            Sheet sheet = book.getSheetAt(0);
            validateData(sheet);
            data = new Instances(sheet.getSheetName(), createAttributes(sheet),
                    sheet.getLastRowNum());

            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                DenseInstance newInstance = new DenseInstance(data.numAttributes());
                newInstance.setDataset(data);
                for (int j = 0; j < data.numAttributes(); j++) {
                    Cell cell = sheet.getRow(i).getCell(j);
                    if (cell == null) {
                        newInstance.setValue(j, Utils.missingValue());
                    } else {
                        switch (cell.getCellTypeEnum()) {
                            case NUMERIC:
                                if (data.attribute(j).isDate()) {
                                    newInstance.setValue(j, cell.getDateCellValue().getTime());
                                } else {
                                    newInstance.setValue(j, cell.getNumericCellValue());
                                }
                                break;

                            case STRING:
                                String stringValue = cell.getStringCellValue().trim();
                                if (StringUtils.isEmpty(stringValue)) {
                                    newInstance.setValue(j, Utils.missingValue());
                                } else {
                                    newInstance.setValue(j, stringValue);
                                }
                                break;

                            case BOOLEAN:
                                String val = String.valueOf(cell.getBooleanCellValue());
                                newInstance.setValue(j, val);
                                break;

                            case BLANK:
                                newInstance.setValue(j, Utils.missingValue());
                                break;

                            default:
                                throw new IllegalArgumentException(FileDataDictionary.BAD_CELL_VALUES);

                        }
                    }
                }
                data.add(newInstance);
            }
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!FileUtils.isXlsExtension(resource.getFile())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    private int getNumColumns(Sheet sheet) {
        return sheet.getRow(HEADER_INDEX).getPhysicalNumberOfCells();
    }

    private String getColumnName(Sheet sheet, int i) {
        return sheet.getRow(HEADER_INDEX).getCell(i).getStringCellValue().trim();
    }

    private ArrayList<Attribute> createAttributes(Sheet sheet) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < getNumColumns(sheet); i++) {
            ArrayList<String> values = new ArrayList<>();
            int attributeType = Attribute.NUMERIC;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Cell cell = sheet.getRow(j).getCell(i);
                if (cell != null && !cell.getCellTypeEnum().equals(CellType.BLANK)) {
                    CellType cellType = cell.getCellTypeEnum();
                    if (cellType.equals(CellType.STRING)) {
                        String stringValue = cell.getStringCellValue().trim();
                        if (!StringUtils.isEmpty(stringValue) && !values.contains(stringValue)) {
                            values.add(stringValue);
                            attributeType = Attribute.NOMINAL;
                        }
                    } else if (cellType.equals(CellType.BOOLEAN)) {
                        String booleanValue = String.valueOf(cell.getBooleanCellValue());
                        if (!values.contains(booleanValue)) {
                            values.add(booleanValue);
                            attributeType = Attribute.NOMINAL;
                        }
                    } else if (cellType.equals(CellType.NUMERIC) && DateUtil.isCellDateFormatted(cell)) {
                        attributeType = Attribute.DATE;
                    }
                }
            }
            attributes.add(createAttribute(attributeType, getColumnName(sheet, i), values));
        }
        return attributes;
    }

    private Attribute createAttribute(int attributeType, String name, ArrayList<String> values) {
        switch (attributeType) {
            case Attribute.NOMINAL:
                return new Attribute(name, values);
            case Attribute.DATE:
                return new Attribute(name, getDateFormat());
            default:
                return new Attribute(name);
        }
    }

    private void validateData(Sheet sheet) {
        int headerSize = sheet.getRow(HEADER_INDEX).getLastCellNum();
        if (headerSize > sheet.getRow(HEADER_INDEX).getPhysicalNumberOfCells()) {
            throw new IllegalArgumentException(FileDataDictionary.EMPTY_COLUMNS_ERROR);
        }
        for (int i = 0; i < getNumColumns(sheet); i++) {
            CellType cellType = null;
            for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
                Row row = sheet.getRow(j);
                if (row == null) {
                    throw new IllegalArgumentException(FileDataDictionary.BAD_DATA_FORMAT);
                }
                if (row.getPhysicalNumberOfCells() > headerSize) {
                    throw new IllegalArgumentException(FileDataDictionary.HEADER_ERROR);
                }
                Cell cell = row.getCell(i);
                if (cell != null) {
                    CellType cellTypeEnum = cell.getCellTypeEnum();
                    if (!AVAILABLE_CELL_TYPES.contains(cellTypeEnum)) {
                        throw new IllegalArgumentException(FileDataDictionary.BAD_CELL_VALUES);
                    }
                    if (isCellTypeNotEquals(cellType, cellTypeEnum)) {
                        throw new IllegalArgumentException(String.format(
                                FileDataDictionary.DIFFERENT_DATA_TYPES_IN_COLUMN_ERROR_FORMAT, i));
                    } else {
                        cellType = cell.getCellTypeEnum();
                    }
                }
            }
        }
    }

    private boolean isCellTypeNotEquals(CellType cellTypeA, CellType cellTypeB) {
        return cellTypeA != null && !cellTypeA.equals(CellType.BLANK) && !cellTypeB.equals(CellType.BLANK) &&
                !cellTypeA.equals(cellTypeB);
    }
}
