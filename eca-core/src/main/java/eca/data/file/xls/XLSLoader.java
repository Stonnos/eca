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
import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.CellType;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.ReadingOptions;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static eca.data.file.FileDataDictionary.EMPTY_COLUMNS_ERROR;

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
            EnumSet.of(CellType.STRING, CellType.NUMBER, CellType.EMPTY, CellType.BOOLEAN);

    private static final List<String> EXCEL_DATE_FORMATS = List.of("dd-mm-yyyy h:mm:ss", "dd-mm-yyyy h:mm");

    @Override
    public Instances loadInstances() throws Exception {
        ReadingOptions readingOptions = new ReadingOptions(true, false);
        try (InputStream inputStream = getSource().openInputStream();
             ReadableWorkbook book = new ReadableWorkbook(inputStream, readingOptions)) {
            Sheet sheet = book.getFirstSheet();
            List<Row> rows = readRows(sheet);
            validateRows(rows);
            return createInstances(sheet, rows);
        }
    }

    @Override
    protected void validateSource(DataResource resource) {
        super.validateSource(resource);
        if (!FileUtils.isXlsExtension(resource.getFile())) {
            throw new IllegalArgumentException(String.format("Unexpected file '%s' extension!", resource.getFile()));
        }
    }

    private Instances createInstances(Sheet sheet, List<Row> rows) {
        Instances data = new Instances(sheet.getName(), createAttributes(rows), rows.size());
        for (int i = 1; i < rows.size(); i++) {
            DenseInstance newInstance = new DenseInstance(data.numAttributes());
            newInstance.setDataset(data);
            for (int j = 0; j < data.numAttributes(); j++) {
                Cell cell = rows.get(i).getCell(j);
                if (cell == null) {
                    newInstance.setValue(j, Utils.missingValue());
                } else {
                    switch (cell.getType()) {
                        case NUMBER:
                            if (data.attribute(j).isDate()) {
                                newInstance.setValue(j,
                                        cell.asDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                            } else {
                                newInstance.setValue(j, cell.asNumber().doubleValue());
                            }
                            break;

                        case STRING:
                            String stringValue = cell.asString().trim();
                            if (StringUtils.isEmpty(stringValue)) {
                                newInstance.setValue(j, Utils.missingValue());
                            } else {
                                newInstance.setValue(j, stringValue);
                            }
                            break;

                        case BOOLEAN:
                            String val = String.valueOf(cell.asBoolean());
                            newInstance.setValue(j, val);
                            break;

                        case EMPTY:
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
        return data;
    }

    private ArrayList<Attribute> createAttributes(List<Row> rows) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        for (int i = 0; i < rows.getFirst().getPhysicalCellCount(); i++) {
            ArrayList<String> values = new ArrayList<>();
            int attributeType = Attribute.NUMERIC;
            for (int j = 1; j < rows.size(); j++) {
                Cell cell = getCell(rows.get(j), i);
                if (cell != null && !cell.getType().equals(CellType.EMPTY)) {
                    CellType cellType = cell.getType();
                    if (cellType.equals(CellType.STRING)) {
                        String stringValue = cell.asString().trim();
                        if (!StringUtils.isEmpty(stringValue) && !values.contains(stringValue)) {
                            values.add(stringValue);
                            attributeType = Attribute.NOMINAL;
                        }
                    } else if (cellType.equals(CellType.BOOLEAN)) {
                        String booleanValue = String.valueOf(cell.asBoolean());
                        if (!values.contains(booleanValue)) {
                            values.add(booleanValue);
                            attributeType = Attribute.NOMINAL;
                        }
                    } else if (isDateCell(cell)) {
                        attributeType = Attribute.DATE;
                    }
                }
            }
            attributes.add(createAttribute(attributeType, rows.getFirst().getCellAsString(i).get(), values));
        }
        return attributes;
    }

    private boolean isDateCell(Cell cell) {
        return cell.getType().equals(CellType.NUMBER) && StringUtils.isNoneBlank(cell.getDataFormatString()) &&
                EXCEL_DATE_FORMATS.contains(cell.getDataFormatString());
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

    private List<Row> readRows(Sheet sheet) throws IOException {
        return sheet.read();
    }

    private void validateRows(List<Row> rows) {
        if (rows.size() <= 1) {
            throw new IllegalArgumentException(FileDataDictionary.EMPTY_DATASET_ERROR);
        }
        Row headerRow = rows.getFirst();
        int headerSize = headerRow.getCellCount();
        if (headerSize > headerRow.getPhysicalCellCount()) {
            throw new IllegalArgumentException(String.format(EMPTY_COLUMNS_ERROR, 0));
        }
        for (int i = 0; i < headerRow.getPhysicalCellCount(); i++) {
            if (StringUtils.isEmpty(headerRow.getCell(i).asString())) {
                throw new IllegalArgumentException(FileDataDictionary.HEADER_ERROR);
            }
            CellType expectedCellType = Optional.ofNullable(getCell(rows.get(1), i)).map(Cell::getType).orElse(null);
            for (int j = 1; j < rows.size(); j++) {
                Row row = rows.get(j);
                if (row == null || row.getCellCount() == 0 || row.getPhysicalCellCount() == 0) {
                    throw new IllegalArgumentException(FileDataDictionary.BAD_DATA_FORMAT);
                }
                Cell cell = getCell(row, i);
                if (cell != null) {
                    CellType actualCellType = cell.getType();
                    if (!AVAILABLE_CELL_TYPES.contains(actualCellType)) {
                        throw new IllegalArgumentException(FileDataDictionary.BAD_CELL_VALUES);
                    }
                    if (isCellTypeNotEquals(expectedCellType, actualCellType)) {
                        throw new IllegalArgumentException(String.format(
                                FileDataDictionary.DIFFERENT_DATA_TYPES_IN_COLUMN_ERROR_FORMAT, i));
                    }
                }
            }
        }
    }

    private Cell getCell(Row row, int cellIndex) {
        return cellIndex <= row.getPhysicalCellCount() ? row.getCell(cellIndex) : null;
    }

    private boolean isCellTypeNotEquals(CellType first, CellType second) {
        return first != null && !first.equals(CellType.EMPTY) && !second.equals(CellType.EMPTY) &&
                !first.equals(second);
    }
}
