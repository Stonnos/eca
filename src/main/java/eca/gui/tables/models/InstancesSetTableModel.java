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

    private final String[] titles = {"Название", "Объекты", "Атрибуты",
            "Классы", "Данные"};

    private ArrayList<Instances> instances = new ArrayList<>();

    public Instances getInstances(int i) {
        return instances.get(i);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return instances.size();
    }

    public void add(Instances data) {
        instances.add(data);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    @Override
    public Object getValueAt(int row, int column) {
        Instances data = instances.get(row);
        switch (column) {
            case 0:
                return data.relationName();
            case 1:
                return data.numInstances();
            case 2:
                return data.numAttributes();
            case 3:
                return data.numClasses();
            default:
                return RESULT_TITLE;
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == BUTTON_INDEX;
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case BUTTON_INDEX:
                return JButton.class;
            default:
                return String.class;
        }
    }

}
