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

import static eca.util.ClassifierNamesFactory.getClassifierName;

/**
 * @author Roman Batygin
 */
public class EnsembleTableModel extends AbstractTableModel {

    public static final int INDEX = 0;
    public static final int CLASSIFIER_INDEX = 1;
    public static final int RESULTS_INDEX = 2;
    public static final String RESULT_TITLE = "Посмотреть";

    private static final String[] TITLES = {"№", "Классификатор", "Результаты"};

    private final List<Classifier> classifierArrayList;

    public EnsembleTableModel(List<Classifier> classifierArrayList) {
        this.classifierArrayList = classifierArrayList;
    }

    public Classifier get(int i) {
        return classifierArrayList.get(i);
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return classifierArrayList.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case INDEX:
                return row;
            case CLASSIFIER_INDEX:
                return getClassifierName(classifierArrayList.get(row));
            case RESULTS_INDEX:
                return RESULT_TITLE;
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case INDEX:
                return Integer.class;
            case CLASSIFIER_INDEX:
                return JTextField.class;
            case RESULTS_INDEX:
                return JButton.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == RESULTS_INDEX;
    }

}
