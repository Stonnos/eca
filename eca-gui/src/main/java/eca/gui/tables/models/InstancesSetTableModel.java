package eca.gui.tables.models;

import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */

public class InstancesSetTableModel extends AbstractTableModel {

    public static final int BUTTON_INDEX = 4;
    public static final String RESULT_TITLE = "Посмотреть";
    private static final String[] TITLES = {"Название", "Объекты", "Атрибуты",
            "Классы", "Данные"};
    private static final int RELATION_NAME_COLUMN_INDEX = 0;
    private static final int NUM_INSTANCES_COLUMN_INDEX = 1;
    private static final int NUM_ATTRIBUTES_COLUMN_INDEX = 2;
    private static final int NUM_CLASSES_COLUMN_INDEX = 3;

    private final ArrayList<Instances> instances = new ArrayList<>();

    public Instances getInstances(int i) {
        return instances.get(i);
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return instances.size();
    }

    public void add(Instances data) {
        instances.add(data);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void clear() {
        instances.forEach(Instances::clear);
        instances.clear();
    }

    @Override
    public Object getValueAt(int row, int column) {
        Instances data = instances.get(row);
        switch (column) {
            case RELATION_NAME_COLUMN_INDEX:
                return data.relationName();
            case NUM_INSTANCES_COLUMN_INDEX:
                return data.numInstances();
            case NUM_ATTRIBUTES_COLUMN_INDEX:
                return data.numAttributes();
            case NUM_CLASSES_COLUMN_INDEX:
                return data.numClasses();
            default:
                return RESULT_TITLE;
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == BUTTON_INDEX;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        return column == BUTTON_INDEX ? JButton.class : String.class;
    }

}
