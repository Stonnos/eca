package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.gui.dictionary.CommonDictionary;
import eca.util.Entry;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Roman Batygin
 */
public class EcaServiceOptionsTableModel extends AbstractTableModel {

    private static final String[] TITLE = {"Параметр", "Значение"};

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final Map<String, String> OPTION_DESCRIPTION_MAP = newHashMap();

    private ArrayList<Entry<String, String>> options = new ArrayList<>();

    static {
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.ECA_SERVICE_ENABLED, "Вкл./выкл. использование сервиса");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_HOST, "Хост брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_PORT, "Порт брокера сообщений");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_USERNAME, "Имя пользователя");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.RABBIT_PASSWORD, "Пароль");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EVALUATION_REQUEST_QUEUE, "Очередь для построения классификаторов");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EVALUATION_OPTIMIZER_REQUEST_QUEUE,
                "Очередь для построения оптимальных классификаторов");
        OPTION_DESCRIPTION_MAP.put(CommonDictionary.EXPERIMENT_REQUEST_QUEUE, "Очередь для построения экспериментов");
    }

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
        Entry<String, String> entry = options.get(row);
        return column == 0 ? OPTION_DESCRIPTION_MAP.getOrDefault(entry.getKey(), entry.getKey()) : entry.getValue();
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
