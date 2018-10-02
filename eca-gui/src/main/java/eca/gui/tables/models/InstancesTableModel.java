/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.text.NumericFormatFactory;
import eca.util.InstancesConverter;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class InstancesTableModel extends AbstractTableModel {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

    private static final String NUMBER = "№";

    private final Instances data;
    private final List<List<Object>> values;
    private final DecimalFormat format = NumericFormatFactory.getInstance();

    private int modificationCount;

    public InstancesTableModel(Instances data, int digits) {
        this.data = data;
        this.format.setMaximumFractionDigits(digits);
        this.values = InstancesConverter.toArray(data, format, SIMPLE_DATE_FORMAT);
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

    /**
     * Remove specified row.
     *
     * @param i - row index
     */
    public void remove(int i) {
        values.remove(i);
        modificationCount++;
        fireTableRowsDeleted(i, i);
    }

    /**
     * Replaced all values.
     *
     * @param j      - column index
     * @param oldVal - old value
     * @param newVal - new value
     */
    public void replace(int j, Object oldVal, Object newVal) {
        for (int i = 0; i < values.size(); i++) {
            if ((oldVal.toString().isEmpty() && getValue(i, j) == null) ||
                    (getValue(i, j) != null && getValue(i, j).equals(oldVal))) {
                setValue(i, j, newVal.toString().isEmpty() ? null : newVal);
            }
        }
        this.fireTableDataChanged();
    }

    /**
     * Clear all data
     */
    public void clear() {
        for (List<Object> row : values) {
            row.clear();
        }
        values.clear();
        modificationCount++;
        fireTableDataChanged();
    }

    /**
     * Removed specified rows.
     *
     * @param indices - rows indices
     */
    public void remove(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            remove(indices[i] - i);
        }
    }

    /**
     * Removes rows with missing values.
     */
    public void removeMissing() {
        ListIterator<List<Object>> iterator = values.listIterator();
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

    /**
     * Sorts data by specified column.
     *
     * @param columnIndex - column index
     * @param ascending   - sorts by ascending?
     */
    public void sort(int columnIndex, boolean ascending) {
        if (columnIndex > 0) {
            values.sort((o1, o2) -> {
                Object x = o1.get(columnIndex - 1);
                Object y = o2.get(columnIndex - 1);
                int sign = ascending ? 1 : -1;
                if (Objects.equals(x, y)) {
                    return 0;
                } else if (x == null) {
                    return ascending ? sign : -sign;
                } else if (y == null) {
                    return ascending ? -sign : sign;
                } else {
                    return sign * x.toString().compareTo(y.toString());
                }
            });
            modificationCount++;
            fireTableDataChanged();
        }
    }

    /**
     * Add sort listener to table header.
     *
     * @param table - table object
     */
    public void addSortListenerToHeader(final JTable table) {
        table.setColumnSelectionAllowed(false);
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            MouseAdapter listMouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TableColumnModel columnModel = table.getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                    int column = table.convertColumnIndexToModel(viewColumn);
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1 && !e.isAltDown() &&
                            column != -1) {
                        int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
                        boolean ascending = (shiftPressed == 0);
                        InstancesTableModel.this.sort(column, ascending);
                    }
                }
            };
            header.addMouseListener(listMouseListener);
        }
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
