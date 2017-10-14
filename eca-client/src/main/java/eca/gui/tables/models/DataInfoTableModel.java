package eca.gui.tables.models;

import eca.statistics.InstancesStatistics;
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
    private static final int STATISTICS_NUM = 7;
    private static final String NUMBER_OF_NUMERIC_ATTRS_TEXT = "Число числовых атрибутов";
    private static final String NUMBER_OF_NOMINAL_ATTRS_TEXT = "Число категориальных атрибутов";
    private static final String MISSING_VALUES_TEXT = "Пропущенные значения";
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
        statistics = new Object[STATISTICS_NUM][title.length];
        int current = 0;
        statistics[current][0] = INSTANCES_NAME_TEXT;
        statistics[current++][1] = data.relationName();

        statistics[current][0] = NUMBER_OF_INSTANCES_TEXT;
        statistics[current++][1] = data.numInstances();

        statistics[current][0] = NUMBER_OF_ATTRS_TEXT;
        statistics[current++][1] = data.numAttributes();

        statistics[current][0] = NUMBER_OF_NUMERIC_ATTRS_TEXT;
        statistics[current++][1] = InstancesStatistics.numNumericAttributes(data);

        statistics[current][0] = NUMBER_OF_NOMINAL_ATTRS_TEXT;
        statistics[current++][1] = InstancesStatistics.numNominalAttributes(data);

        statistics[current][0] = NUMBER_OF_CLASSES_TEXT;
        statistics[current++][1] = data.numClasses();

        statistics[current][0] = MISSING_VALUES_TEXT;
        statistics[current++][1] = InstancesStatistics.hasMissing(data);
    }

}
