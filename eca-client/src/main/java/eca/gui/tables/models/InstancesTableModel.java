/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.text.DateFormat;
import eca.text.NumericFormatFactory;
import eca.util.InstancesConverter;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class InstancesTableModel extends AbstractTableModel {

    private static final String NUMBER = "№";

    private final Instances data;
    private final ArrayList<ArrayList<Object>> values;
    private final DecimalFormat format = NumericFormatFactory.getInstance();

    private int modificationCount;

    public InstancesTableModel(Instances data, int digits) {
        this.data = data;
        this.format.setMaximumFractionDigits(digits);
        this.values = InstancesConverter.toArray(data, format, DateFormat.SIMPLE_DATE_FORMAT);
    }

    public int getModificationCount() {
        return modificationCount;
    }

    public DecimalFormat format() {
        return format;
    }

    public Instances data() {
        return data;
    }

    public void remove(int i) {
        values.remove(i);
        modificationCount++;
        fireTableRowsDeleted(i, i);
    }

    public void replace(int j, Object oldVal, Object newVal) {
        for (int i = 0; i < values.size(); i++) {
            if ((oldVal.toString().isEmpty() && getValue(i, j) == null) ||
                    (getValue(i, j) != null && getValue(i, j).equals(oldVal))) {
                setValue(i, j, newVal.toString().isEmpty() ? null : newVal);
            }
        }
        this.fireTableDataChanged();
    }

    public void clear() {
        for (ArrayList<Object> row : values) {
            row.clear();
        }
        values.clear();
        modificationCount++;
        fireTableDataChanged();
    }

    public void remove(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            remove(indices[i] - i);
        }
    }

    public void removeMissing() {
        ListIterator<ArrayList<Object>> iterator = values.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains(null)) {
                iterator.remove();
                modificationCount++;
            }
        }
        fireTableDataChanged();
    }

    public void add(Object val) {
        ArrayList<Object> row = new ArrayList<>(getColumnCount());
        values.add(row);
        for (int i = 0; i < getColumnCount(); i++) {
            row.add(val);
        }
        modificationCount++;
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    @Override
    public int getColumnCount() {
        return data.numAttributes() + 1;
    }

    @Override
    public int getRowCount() {
        return values.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? row + 1 : getValue(row, column - 1);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String value = aValue.toString().trim();
        setValue(rowIndex, columnIndex - 1, value.isEmpty() ? null : value);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column > 0;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? NUMBER : data.attribute(column - 1).name();
    }

    private Object getValue(int i, int j) {
        return values.get(i).get(j);
    }

    private void setValue(int i, int j, Object val) {
        Object oldVal = getValue(i, j);
        if (!Objects.equals(oldVal, val)) {
            values.get(i).set(j, val);
            modificationCount++;
        }
    }


}
