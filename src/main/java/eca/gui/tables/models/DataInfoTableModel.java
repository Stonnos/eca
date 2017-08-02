package eca.gui.tables.models;

import weka.core.Instances;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */

public class DataInfoTableModel extends AbstractTableModel {

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
        statistica[current][0] = "Данные";
        statistica[current++][1] = data.relationName();

        statistica[current][0] = "Число объектов";
        statistica[current++][1] = data.numInstances();

        statistica[current][0] = "Число атрибутов";
        statistica[current++][1] = data.numAttributes();

        statistica[current][0] = "Число классов";
        statistica[current++][1] = data.numClasses();
    }

}
