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
    private Object[][] statistica;

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
        return statistica.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return statistica[row][column];
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }

    private void init() {
        statistica = new Object[4][title.length];
        int current = 0;
        statistica[current][0] = INSTANCES_NAME_TEXT;
        statistica[current++][1] = data.relationName();

        statistica[current][0] = NUMBER_OF_INSTANCES_TEXT;
        statistica[current++][1] = data.numInstances();

        statistica[current][0] = NUMBER_OF_ATTRS_TEXT;
        statistica[current++][1] = data.numAttributes();

        statistica[current][0] = NUMBER_OF_CLASSES_TEXT;
        statistica[current++][1] = data.numClasses();
    }

}
