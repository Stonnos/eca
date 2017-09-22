/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.dictionary.AttributesTypesDictionary;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */
public class ClassifyInstanceTableModel extends AbstractTableModel {

    public static final int TEXT_INDEX = 4;
    private static final String ANY_NUMBER_TEXT = "Любое число";
    private static final String NOMINAL_ATTR_VALUES_INTERVAL_FORMAT = "Целое от [0,%d]";

    private final Instances data;
    private final Object[] values;
    private final String[] titles = {"№", "Атрибут", "Тип", "Диапазон", "Значение"};

    public ClassifyInstanceTableModel(Instances data) {
        this.data = data;
        values = new Object[data.numAttributes()];
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return data.numAttributes() - 1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == TEXT_INDEX;
    }

    @Override
    public Object getValueAt(int row, int column) {
        int i = row >= data.classIndex() ? row + 1 : row;
        Attribute a = data.attribute(i);
        switch (column) {
            case 0:
                return row + 1;
            case 1:
                return a.name();
            case 2:
                if (a.isDate()) {
                    return AttributesTypesDictionary.DATE;
                } else if (a.isNumeric()) {
                    return AttributesTypesDictionary.NUMERIC;
                } else {
                    return AttributesTypesDictionary.NOMINAL;
                }
            case 3:
                return a.isNominal() ? String.format(NOMINAL_ATTR_VALUES_INTERVAL_FORMAT, a.numValues() - 1)
                        : ANY_NUMBER_TEXT;
            case TEXT_INDEX:
                return values[i];
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == TEXT_INDEX) {
            int i = rowIndex >= data.classIndex() ? rowIndex + 1 : rowIndex;
            values[i] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    public Object[] values() {
        return values;
    }

    public Instances data() {
        return data;
    }

}