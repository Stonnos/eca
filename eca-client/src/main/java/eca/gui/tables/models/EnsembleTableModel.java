/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.ensemble.EnsembleClassifier;
import weka.classifiers.Classifier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
public class EnsembleTableModel extends AbstractTableModel {

    private final String[] titles = {"№", "Классификатор", "Результаты"};
    private final ArrayList<Classifier> struct;

    public static final String RESULT_TITLE = "Посмотреть";

    public EnsembleTableModel(EnsembleClassifier struct) throws Exception {
        this.struct = struct.getStructure();
    }

    public Classifier get(int i) {
        return struct.get(i);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return struct.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return row;
            case 1:
                return struct.get(row).getClass().getSimpleName();
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
