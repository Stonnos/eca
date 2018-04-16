package eca.gui.tables.models;

import eca.config.ApplicationConfigService;
import eca.config.EcaServiceConfig;
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

    private static final ApplicationConfigService CONFIG_SERVICE =
            ApplicationConfigService.getApplicationConfigService();
    private static EcaServiceConfig ecaServiceConfig;

    static {
        ecaServiceConfig = CONFIG_SERVICE.getEcaServiceConfig();
    }

    private ArrayList<Entry> options = new ArrayList<>();

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
        Entry entry = options.get(rowIndex);
        entry.setValue(aValue.toString());
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return TITLE[column];
    }

    public Iterator<Entry> getOptions() {
        return options.iterator();
    }

    private void init() {
        options.add(new Entry(CommonDictionary.ECA_SERVICE_ENABLED, ecaServiceConfig.getEnabled().toString()));
        options.add(new Entry(CommonDictionary.ECA_SERVICE_URL, ecaServiceConfig.getEvaluationUrl()));
        options.add(new Entry(CommonDictionary.ECA_SERVICE_EXPERIMENT_URL, ecaServiceConfig.getExperimentUrl()));
    }
}
