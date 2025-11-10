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

    private final ArrayList<Object> selectedAttrs;
    private final ArrayList<Object> attrTypes;
    private final ArrayList<String> attrNames;
    private final ArrayList<String> initialTypes;

    private int modificationCount;

    public AttributesTableModel(Instances data) {
        this.selectedAttrs = new ArrayList<>(data.numAttributes());
        this.attrTypes = new ArrayList<>(data.numAttributes());
        this.attrNames = new ArrayList<>(data.numAttributes());
        this.initialTypes = new ArrayList<>(data.numAttributes());
        for (int i = 0; i < data.numAttributes(); i++) {
            this.selectedAttrs.add(true);
            this.attrTypes.add(AttributesTypesDictionary.getType(data.attribute(i)));
            this.attrNames.add(data.attribute(i).name());
            this.initialTypes.add(AttributesTypesDictionary.getType(data.attribute(i)));
        }
    }

    public void clear() {
        selectedAttrs.clear();
        attrTypes.clear();
        attrNames.clear();
        initialTypes.clear();
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
        return attrNames.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == EDIT_INDEX || (column == LIST_INDEX && isAttributeSelected(row));
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return switch (column) {
            case 0 -> Integer.class;
            case 1 -> Boolean.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int row, int column) {
        return switch (column) {
            case 0 -> row + 1;
            case EDIT_INDEX -> selectedAttrs.get(row);
            case 2 -> attrNames.get(row);
            case LIST_INDEX -> attrTypes.get(row);
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == EDIT_INDEX) {
            selectedAttrs.set(rowIndex, aValue);
            modificationCount += Boolean.FALSE.equals(aValue) ? 1 : -1;
        } else if (columnIndex == LIST_INDEX) {
            Object oldValue = attrTypes.get(rowIndex);
            attrTypes.set(rowIndex, aValue);
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
            setValueAt(initialTypes.get(i), i, LIST_INDEX);
        }
    }

    public void renameAttribute(int index, String newName) {
        if (!Objects.equals(attrNames.get(index), newName)) {
            attrNames.set(index, newName);
            fireTableRowsUpdated(index, index);
            modificationCount++;
        }
    }

    public String getAttributeName(int index) {
        return attrNames.get(index);
    }
}
