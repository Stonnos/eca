package eca.gui.tables.models;

import weka.core.Instances;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */

public class DataInfoTableModel extends AbstractTableModel {

    private static final String INSTANCES_NAME_TEXT = "Данные";
    private static final String NUMBER_OF_INSTANCES_TEXT = "Число объектов";
    private static final String NUMBER_OF_ATTRS_TEXT = "Число атрибутов";
    private static final String NUMBER_OF_CLASSES_TEXT = "Число классов";
    private Object[][] statistics;

    private final String[] title = {"Статистика", "Значение"};

    private Instances data;

    public DataInfoTableModel(Instances data) {
        this.data = data;
        this.init();
    }

    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public int getRowCount() {
        return statistics.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return statistics[row][column];
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }

    private void init() {
        statistics = new Object[4][title.length];
        int current = 0;
        statistics[current][0] = INSTANCES_NAME_TEXT;
        statistics[current++][1] = data.relationName();

        statistics[current][0] = NUMBER_OF_INSTANCES_TEXT;
        statistics[current++][1] = data.numInstances();

        statistics[current][0] = NUMBER_OF_ATTRS_TEXT;
        statistics[current++][1] = data.numAttributes();

        statistics[current][0] = NUMBER_OF_CLASSES_TEXT;
        statistics[current++][1] = data.numClasses();
    }

}
