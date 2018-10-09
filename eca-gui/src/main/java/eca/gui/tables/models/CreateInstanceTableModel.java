package eca.gui.tables.models;

import eca.config.ConfigurationService;
import eca.dictionary.AttributesTypesDictionary;
import eca.util.Entry;
import weka.core.Attribute;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class CreateInstanceTableModel extends AbstractTableModel {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String[] TITLES = {"№", "Атрибут", "Диапазон значений", "Значение"};

    private static final String ANY_NUMBER_TEXT = "Любое число";
    private static final String DATE_ATTR_FORMAT = "Дата в формате: %s";
    private static final String ANY_STRING = "Любая строка";

    public static final int NUMERATOR_COLUMN_INDEX = 0;
    public static final int ATTRIBUTE_TYPE_COLUMN_INDEX = 1;
    public static final int ATTRIBUTE_INFO_COLUMN_INDEX = 2;
    public static final int INPUT_TEXT_COLUMN_INDEX = 3;

    private final List<Entry<String, Integer>> attributes;
    private final String[] values;

    public CreateInstanceTableModel(List<Entry<String, Integer>> attributes) {
        this.attributes = attributes;
        values = new String[attributes.size()];
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return attributes.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == INPUT_TEXT_COLUMN_INDEX;
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case NUMERATOR_COLUMN_INDEX:
                return row + 1;
            case ATTRIBUTE_TYPE_COLUMN_INDEX:
                return attributes.get(row).getKey();
            case ATTRIBUTE_INFO_COLUMN_INDEX:
                switch (attributes.get(row).getValue()) {
                    case Attribute.DATE:
                        return String.format(DATE_ATTR_FORMAT, CONFIG_SERVICE.getApplicationConfig().getDateFormat());
                    case Attribute.NUMERIC:
                        return ANY_NUMBER_TEXT;
                    default:
                        return ANY_STRING;
                }
            case INPUT_TEXT_COLUMN_INDEX:
                return values[row];
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == INPUT_TEXT_COLUMN_INDEX) {
            values[rowIndex] = aValue != null ? aValue.toString() : null;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    public List<Entry<String, Integer>> getAttributes() {
        return attributes;
    }

    public String[] getValues() {
        return values;
    }

}
