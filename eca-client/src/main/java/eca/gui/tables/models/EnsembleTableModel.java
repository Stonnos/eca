/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import weka.classifiers.Classifier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class EnsembleTableModel extends AbstractTableModel {

    private final String[] titles = {"№", "Классификатор", "Результаты"};
    private final List<Classifier> classifierArrayList;

    public static final String RESULT_TITLE = "Посмотреть";

    public EnsembleTableModel(List<Classifier> classifierArrayList) throws Exception {
        this.classifierArrayList = classifierArrayList;
    }

    public Classifier get(int i) {
        return classifierArrayList.get(i);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return classifierArrayList.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return row;
            case 1:
                return classifierArrayList.get(row).getClass().getSimpleName();
            default:
                return RESULT_TITLE;
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return Integer.class;
            case 1:
                return JTextField.class;
            case 2:
                return JButton.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2;
    }

}
