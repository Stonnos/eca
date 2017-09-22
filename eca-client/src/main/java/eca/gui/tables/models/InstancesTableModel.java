/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.converters.InstancesConverter;
import eca.text.NumericFormat;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * @author Roman Batygin
 */
public class InstancesTableModel extends AbstractTableModel {

    private static final int MAX_FRACTION_DIGITS = 13;
    private static final String NUMBER = "№";

    private final Instances data;
    private final ArrayList<ArrayList<Object>> values;
    private final DecimalFormat format = NumericFormat.getInstance();

    public InstancesTableModel(Instances data) {
        this.data = data;
        format.setMaximumFractionDigits(MAX_FRACTION_DIGITS);
        values = InstancesConverter.toArray(data, format);
    }

    public DecimalFormat format() {
        return format;
    }

    public Instances data() {
        return data;
    }

    public void remove(int i) {
        values.remove(i);
        fireTableRowsDeleted(i, i);
    }

    public void replace(int j, Object oldVal, Object newVal) {
        for (int i = 0; i < values.size(); i++) {
            if ((oldVal.toString().isEmpty() && get(i, j) == null) ||
                    (get(i, j) != null && get(i, j).equals(oldVal))) {
                set(i, j, newVal.toString().isEmpty() ? null : newVal);
            }
        }
        this.fireTableDataChanged();
    }

    public void clear() {
        for (ArrayList<Object> row : values) {
            row.clear();
        }
        values.clear();
        fireTableDataChanged();
    }

    public void remove(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            remove(indices[i] - i);
        }
    }

    public void removeMissing() {
        ListIterator<ArrayList<Object>> iter = values.listIterator();
        while (iter.hasNext()) {
            if (iter.next().contains(null)) {
                iter.remove();
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
        return column == 0 ? row + 1 : get(row, column - 1);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String value = aValue.toString().trim();
        set(rowIndex, columnIndex - 1, value.isEmpty() ? null : value);
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

    private Object get(int i, int j) {
        return values.get(i).get(j);
    }

    private void set(int i, int j, Object val) {
        values.get(i).set(j, val);
    }


}
