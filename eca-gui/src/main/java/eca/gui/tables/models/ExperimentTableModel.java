/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.core.evaluation.EvaluationResults;
import eca.dataminer.ClassifierComparator;
import eca.text.NumericFormatFactory;
import weka.classifiers.Classifier;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class ExperimentTableModel extends AbstractTableModel {

    public static final int INDEX = 0;
    public static final int CLASSIFIER_INDEX = 1;
    public static final int ACCURACY_INDEX = 2;
    public static final int RESULTS_INDEX = 3;
    public static final String RESULT_TITLE = "Посмотреть";

    private static final ClassifierComparator CLASSIFIER_COMPARATOR = new ClassifierComparator();
    private static final String[] TITLES = {"№", "Классификатор", "Точность, %", "Результаты"};

    private List<EvaluationResults> experiment;

    private final DecimalFormat format = NumericFormatFactory.getInstance();

    public ExperimentTableModel(List<EvaluationResults> experiment, int digits) {
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
        return TITLES.length;
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
            case INDEX:
                return row;
            case CLASSIFIER_INDEX:
                return experiment.get(row).getClassifier().getClass().getSimpleName();
            case ACCURACY_INDEX:
                return format.format(experiment.get(row).getEvaluation().pctCorrect());
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
            case ACCURACY_INDEX:
                return String.class;
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
