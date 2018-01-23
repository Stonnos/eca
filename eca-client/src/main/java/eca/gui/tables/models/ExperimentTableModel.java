/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.text.NumericFormat;
import weka.classifiers.Classifier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class ExperimentTableModel extends AbstractTableModel {

    public static final String RESULT_TITLE = "Посмотреть";

    private static final ClassifierComparator CLASSIFIER_COMPARATOR = new ClassifierComparator();

    private final String[] titles = {"№", "Классификатор", "Точность, %", "Результаты"};
    private List<EvaluationResults> experiment;

    private final DecimalFormat format = NumericFormat.getInstance();

    public ExperimentTableModel(List<EvaluationResults> experiment, int digits) throws Exception {
        this.experiment = experiment;
        format.setMaximumFractionDigits(digits);
    }

    public Classifier getClassifier(int i) {
        return experiment.get(i).getClassifier();
    }

    public List<EvaluationResults> getExperiment() {
        return experiment;
    }

    public EvaluationResults get(int i) {
        return experiment.get(i);
    }

    public int digits() {
        return format.getMaximumFractionDigits();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return experiment.size();
    }

    public void add(EvaluationResults val) {
        experiment.add(val);
        fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
    }

    public void clear() {
        experiment.clear();
        fireTableDataChanged();
    }

    public void sort() {
        experiment.sort(CLASSIFIER_COMPARATOR);
        fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return row;
            case 1:
                return experiment.get(row).getClassifier().getClass().getSimpleName();
            case 2:
                return format.format(experiment.get(row).getEvaluation().pctCorrect());
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
            case 3:
                return String.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3;
    }

}
