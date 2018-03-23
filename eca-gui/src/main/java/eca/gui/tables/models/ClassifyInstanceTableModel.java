/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.dictionary.AttributesTypesDictionary;
import eca.text.DateFormat;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */
public class ClassifyInstanceTableModel extends AbstractTableModel {

    private static final String[] TITLES = {"№", "Атрибут", "Тип", "Диапазон значений", "Значение"};

    public static final int NUMERATOR_COLUMN_INDEX = 0;
    public static final int ATTRIBUTE_INFO_COLUMN_INDEX = 1;
    public static final int ATTRIBUTE_TYPE_COLUMN_INDEX = 2;
    public static final int ADDITIONAL_INFO_COLUMN_INDEX = 3;
    public static final int INPUT_TEXT_COLUMN_INDEX = 4;

    private static final String ANY_NUMBER_TEXT = "Любое число";
    private static final String NOMINAL_ATTR_VALUES_INTERVAL_FORMAT = "Целое от [0,%d]";
    private static final String DATE_ATTR_FORMAT = "Дата в формате: %s";

    private final Instances data;
    private final Object[] values;

    public ClassifyInstanceTableModel(Instances data) {
        this.data = data;
        values = new Object[data.numAttributes()];
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return data.numAttributes() - 1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == INPUT_TEXT_COLUMN_INDEX;
    }

    @Override
    public Object getValueAt(int row, int column) {
        int i = row >= data.classIndex() ? row + 1 : row;
        Attribute a = data.attribute(i);
        switch (column) {
            case NUMERATOR_COLUMN_INDEX:
                return row + 1;
            case ATTRIBUTE_INFO_COLUMN_INDEX:
                return a.name();
            case ATTRIBUTE_TYPE_COLUMN_INDEX:
                if (a.isDate()) {
                    return AttributesTypesDictionary.DATE;
                } else if (a.isNumeric()) {
                    return AttributesTypesDictionary.NUMERIC;
                } else {
                    return AttributesTypesDictionary.NOMINAL;
                }
            case ADDITIONAL_INFO_COLUMN_INDEX:
                if (a.isDate()) {
                    return String.format(DATE_ATTR_FORMAT, DateFormat.DATE_FORMAT);
                }
                else if (a.isNumeric()) {
                    return ANY_NUMBER_TEXT;
                } else {
                    return String.format(NOMINAL_ATTR_VALUES_INTERVAL_FORMAT, a.numValues() - 1);
                }
            case INPUT_TEXT_COLUMN_INDEX:
                return values[i];
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == INPUT_TEXT_COLUMN_INDEX) {
            int i = rowIndex >= data.classIndex() ? rowIndex + 1 : rowIndex;
            values[i] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    public Object[] values() {
        return values;
    }

    public Instances data() {
        return data;
    }

}
