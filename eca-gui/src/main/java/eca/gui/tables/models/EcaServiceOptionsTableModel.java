package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.gui.dictionary.CommonDictionary;
import eca.util.Entry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Roman Batygin
 */
public class EcaServiceOptionsTableModel extends AbstractTableModel {

    private static final String[] TITLE = {"Параметр", "Значение"};

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private ArrayList<Entry<String, String>> options = new ArrayList<>();

    public EcaServiceOptionsTableModel() {
        init();
    }

    @Override
    public int getColumnCount() {
        return TITLE.length;
    }

    @Override
    public int getRowCount() {
        return options.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Entry entry = options.get(row);
        return column == 0 ? entry.getKey() : entry.getValue();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Entry<String, String> entry = options.get(rowIndex);
        entry.setValue(aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return TITLE[column];
    }

    public Iterator<Entry<String, String>> getOptions() {
        return options.iterator();
    }

    private void init() {
        options.add(new Entry<>(CommonDictionary.ECA_SERVICE_ENABLED,
                CONFIG_SERVICE.getEcaServiceConfig().getEnabled().toString()));
        options.add(new Entry<>(CommonDictionary.ECA_API_URL, CONFIG_SERVICE.getEcaServiceConfig().getApiUrl()));
    }
}
