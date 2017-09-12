/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.gui.dictionary.AttributesTypesDictionary;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
public class AttributesTableModel extends AbstractTableModel {

    public static final int EDIT_INDEX = 1;
    public static final int LIST_INDEX = 3;

    private final String[] title = {"№", StringUtils.EMPTY, "Атрибут", "Тип"};
    private final ArrayList<Object> selectedAttr;
    private final ArrayList<Object> attrType;

    private final Instances data;

    public AttributesTableModel(Instances data) {
        this.data = data;
        selectedAttr = new ArrayList<>(data.numAttributes());
        attrType = new ArrayList<>(data.numAttributes());
        //------------------------------------
        for (int i = 0; i < data.numAttributes(); i++) {
            selectedAttr.add(true);
            attrType.add(AttributesTypesDictionary.getType(data.attribute(i)));
        }
    }


    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public int getRowCount() {
        return data.numAttributes();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == EDIT_INDEX ||
                (column == LIST_INDEX && isAttributeSelected(row));
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
        } else if (columnIndex == LIST_INDEX) {
            attrType.set(rowIndex, aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }

    public final boolean isAttributeSelected(int i) {
        return (Boolean) getValueAt(i, EDIT_INDEX);
    }

    public final boolean isNumeric(int i) {
        return ((String) getValueAt(i, LIST_INDEX)).equals(AttributesTypesDictionary.NUMERIC);
    }

    public final boolean isDate(int i) {
        return ((String) getValueAt(i, LIST_INDEX)).equals(AttributesTypesDictionary.DATE);
    }

    public final void selectAllAttributes() {
        for (int i = 0; i < getRowCount(); i++) {
            setValueAt(true, i, EDIT_INDEX);
        }
    }

    public final void resetValues() {
        for (int i = 0; i < this.getRowCount(); i++) {
            setValueAt(true, i, EDIT_INDEX);
            setValueAt(AttributesTypesDictionary.getType(data.attribute(i)), i, LIST_INDEX);
        }
    }

    public Instances data() {
        return data;
    }


}
