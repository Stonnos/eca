package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.gui.dictionary.CommonDictionary;
import eca.util.Entry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

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

    public List<Entry<String, String>> getOptions() {
        return options;
    }

    private void init() {
        options.add(new Entry<>(CommonDictionary.ECA_SERVICE_ENABLED,
                CONFIG_SERVICE.getEcaServiceConfig().getEnabled().toString()));
        options.add(new Entry<>(CommonDictionary.RABBIT_HOST, CONFIG_SERVICE.getEcaServiceConfig().getHost()));
        options.add(new Entry<>(CommonDictionary.RABBIT_PORT,
                String.valueOf(CONFIG_SERVICE.getEcaServiceConfig().getPort())));
        options.add(new Entry<>(CommonDictionary.RABBIT_USERNAME, CONFIG_SERVICE.getEcaServiceConfig().getUsername()));
        options.add(new Entry<>(CommonDictionary.RABBIT_PASSWORD, CONFIG_SERVICE.getEcaServiceConfig().getPassword()));
        options.add(new Entry<>(CommonDictionary.EVALUATION_REQUEST_QUEUE,
                CONFIG_SERVICE.getEcaServiceConfig().getEvaluationRequestQueue()));
        options.add(new Entry<>(CommonDictionary.EVALUATION_OPTIMIZER_REQUEST_QUEUE,
                CONFIG_SERVICE.getEcaServiceConfig().getEvaluationOptimizerRequestQueue()));
        options.add(new Entry<>(CommonDictionary.EXPERIMENT_REQUEST_QUEUE,
                CONFIG_SERVICE.getEcaServiceConfig().getExperimentRequestQueue()));
    }
}
