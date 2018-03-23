/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.dictionary.AttributesTypesDictionary;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class AttributesTableModel extends AbstractTableModel {

    private static final String[] TITLES = {"№", StringUtils.EMPTY, "Атрибут", "Тип"};

    public static final int EDIT_INDEX = 1;
    public static final int LIST_INDEX = 3;

    private final ArrayList<Object> selectedAttr;
    private final ArrayList<Object> attrType;

    private final Instances data;

    private int modificationCount;

    public AttributesTableModel(Instances data) {
        this.data = data;
        this.selectedAttr = new ArrayList<>(data.numAttributes());
        this.attrType = new ArrayList<>(data.numAttributes());
        for (int i = 0; i < data.numAttributes(); i++) {
            this.selectedAttr.add(true);
            this.attrType.add(AttributesTypesDictionary.getType(data.attribute(i)));
        }
    }

    public Instances data() {
        return data;
    }

    public int getModificationCount() {
        return modificationCount;
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return data.numAttributes();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == EDIT_INDEX || (column == LIST_INDEX && isAttributeSelected(row));
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return Integer.class;
            case 1:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return row + 1;

            case EDIT_INDEX:
                return selectedAttr.get(row);

            case 2:
                return data.attribute(row).name();

            case LIST_INDEX:
                return attrType.get(row);

            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == EDIT_INDEX) {
            selectedAttr.set(rowIndex, aValue);
            modificationCount += Boolean.FALSE.equals(aValue) ? 1 : -1;
        } else if (columnIndex == LIST_INDEX) {
            Object oldValue = attrType.get(rowIndex);
            attrType.set(rowIndex, aValue);
            if (!Objects.equals(aValue, oldValue)) {
                modificationCount++;
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    public boolean isAttributeSelected(int i) {
        return (Boolean) getValueAt(i, EDIT_INDEX);
    }

    public boolean isNumeric(int i) {
        return getValueAt(i, LIST_INDEX).equals(AttributesTypesDictionary.NUMERIC);
    }

    public boolean isDate(int i) {
        return getValueAt(i, LIST_INDEX).equals(AttributesTypesDictionary.DATE);
    }

    public void selectAllAttributes() {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(true, i, EDIT_INDEX);
        }
    }

    public void resetValues() {
        for (int i = 0; i < this.getRowCount(); i++) {
            setValueAt(true, i, EDIT_INDEX);
            setValueAt(AttributesTypesDictionary.getType(data.attribute(i)), i, LIST_INDEX);
        }
    }

    public void renameAttribute(int index, String newName) {
        if (!Objects.equals(data.attribute(index).name(), newName)) {
            data.renameAttribute(index, newName);
            fireTableRowsUpdated(index, index);
            modificationCount++;
        }
    }

}
