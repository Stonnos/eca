package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.model.DataSetList;
import eca.text.NumericFormatFactory;
import eca.util.InstancesConverter;
import lombok.Getter;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class InstancesTableModel extends AbstractTableModel {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String NUMBER = "№";

    @Getter
    private final DataSetList dataSetList;

    private final DecimalFormat format = NumericFormatFactory.getInstance();

    @Getter
    private int modificationCount;

    public InstancesTableModel(Instances data, int digits) {
        this.format.setMaximumFractionDigits(digits);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());
        this.dataSetList = InstancesConverter.convertToDataSet(data, format, simpleDateFormat);
    }

    public DecimalFormat format() {
        return format;
    }

    /**
     * Remove specified row.
     *
     * @param i - row index
     */
    public void remove(int i) {
        dataSetList.remove(i);
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
        for (int i = 0; i < dataSetList.size(); i++) {
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
        clearRows();
        dataSetList.clear();
        modificationCount++;
        fireTableDataChanged();
    }

    /**
     * Clear all data fully.
     */
    public void clearFully() {
        clearRows();
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
        ListIterator<List<Object>> iterator = dataSetList.getValues().listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().contains(null)) {
                iterator.remove();
                modificationCount++;
            }
        }
        fireTableDataChanged();
    }

    /**
     * Adds row.
     *
     * @param row - values list
     */
    public void addRow(List<Object> row) {
        dataSetList.addRow(row);
        modificationCount++;
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    /**
     * Sorts data by specified column and attribute type.
     *
     * @param columnIndex   - column index
     * @param attributeType - attribute type
     * @param ascending     - sorts by ascending?
     */
    public void sort(final int columnIndex, final int attributeType, final boolean ascending) {
        dataSetList.sort(columnIndex - 1, attributeType, ascending);
        modificationCount++;
        fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return dataSetList.getAttributes().size() + 1;
    }

    @Override
    public int getRowCount() {
        return dataSetList.size();
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
        return column == 0 ? NUMBER : dataSetList.getAttributes().get(column - 1);
    }

    private Object getValue(int i, int j) {
        return dataSetList.getValue(i, j);
    }

    private void setValue(int i, int j, Object val) {
        Object oldVal = getValue(i, j);
        if (!Objects.equals(oldVal, val)) {
            dataSetList.setValue(i, j, val);
            modificationCount++;
        }
    }

    private void clearRows() {
        dataSetList.clear();
    }
}
