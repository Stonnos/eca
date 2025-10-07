package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.gui.tables.PageableTable;
import eca.model.DataSetList;
import eca.text.NumericFormatFactory;
import eca.util.InstancesConverter;
import lombok.Getter;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * @author Roman Batygin
 */
public class InstancesTableModel extends AbstractTableModel implements PageableTable {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String NUMBER = "№";
    private static final int FIRST_PAGE = 1;
    private static final int PAGE_SIZE = 500;

    @Getter
    private DataSetList dataSetList;

    private final DecimalFormat format = NumericFormatFactory.getInstance();

    @Getter
    private int modificationCount;

    private int page = 1;

    private final List<ActionListener> dataChangeActionListeners = new ArrayList<>();

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
        dataSetList.remove(i + getOffset());
        modificationCount++;
        if (getOffset() >= dataSetList.size()) {
            previousPage();
        }
        notifyListeners();
        fireTableDataChanged();
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
        dataSetList.clear();
        modificationCount++;
        setFirstPage();
    }

    /**
     * Clear all data fully.
     */
    public void clearFully() {
        dataSetList.clear();
        dataChangeActionListeners.clear();
        dataSetList = null;
    }

    /**
     * Removed specified rows.
     *
     * @param indices - rows indices
     */
    public void remove(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
            dataSetList.remove(indices[i] - i + getOffset());
            modificationCount++;
        }
        if (getOffset() >= dataSetList.size()) {
            previousPage();
        }
        notifyListeners();
        fireTableDataChanged();
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
        setFirstPage();
    }

    /**
     * Adds row.
     *
     * @param row - values list
     */
    public void addRow(List<Object> row) {
        dataSetList.addRow(row);
        modificationCount++;
        if (getPage() == totalPages() && getRowCount() < pageSize()) {
            fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
        }
        notifyListeners();
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
        setFirstPage();
    }

    @Override
    public int getColumnCount() {
        return dataSetList.getAttributes().size() + 1;
    }

    @Override
    public int getRowCount() {
        if (getPage() == totalPages()) {
            int offset = getOffset();
            return dataSetList.size() - offset;
        } else {
            return dataSetList.size() > 0 ? pageSize(): 0;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        int offset = getOffset();
        return column == 0 ? row + offset + 1 : getValue(row + offset, column - 1);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String value = aValue.toString().trim();
        int offset = getOffset();
        setValue(offset + rowIndex, columnIndex - 1, value.isEmpty() ? null : value);
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

    private int getOffset() {
        return (getPage() - 1) * pageSize();
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

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int totalPages() {
        return (int) Math.ceil((double) dataSetList.getValues().size() / pageSize());
    }

    @Override
    public int pageSize() {
        return PAGE_SIZE;
    }

    @Override
    public void nextPage() {
        if (page < totalPages()) {
            ++page;
            fireTableDataChanged();
        }
    }

    @Override
    public void previousPage() {
        if (page > FIRST_PAGE) {
            --page;
            fireTableDataChanged();
        }
    }

    @Override
    public void firstPage() {
        page = FIRST_PAGE;
        fireTableDataChanged();
    }

    @Override
    public void lastPage() {
        page = totalPages();
        fireTableDataChanged();
    }

    private void setFirstPage() {
        firstPage();
        notifyListeners();
    }

    private void notifyListeners() {
        dataChangeActionListeners.forEach(actionListener -> actionListener.actionPerformed(new ActionEvent(this, 0, "")));
    }

    @Override
    public void addDataChangeActionListener(ActionListener actionListener) {
        this.dataChangeActionListeners.add(actionListener);
    }
}
