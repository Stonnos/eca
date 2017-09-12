/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.gui.text.NumericFormat;
import eca.regression.Logistic;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class LogisticCoefficientsTableModel extends AbstractTableModel {

    private static final String INTERCEPT = "Intercept";
    private static final String ATTR_TEXT = "Атрибут";
    private static final String CLASS_FORMAT = "Класс %d";
    private final Logistic logistic;
    private Instances data;
    private String[] titles;
    private final DecimalFormat format = NumericFormat.getInstance();
    private final NominalToBinary ntbFilter = new NominalToBinary();
    private final ReplaceMissingValues missValFilter = new ReplaceMissingValues();
    private final RemoveUseless uselessFilter = new RemoveUseless();

    public LogisticCoefficientsTableModel(Logistic logistic, Instances data, int digits) throws Exception {
        this.logistic = logistic;
        this.data = data;
        this.createNames();
        this.filterInstances();
        format.setMaximumFractionDigits(digits);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return logistic.coefficients().length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? (row == 0 ? INTERCEPT : data.attribute(row - 1).name())
                : format.format(logistic.coefficients()[row][column - 1]);
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void filterInstances() throws Exception {
        missValFilter.setInputFormat(data);
        data = Filter.useFilter(data, missValFilter);
        uselessFilter.setInputFormat(data);
        data = Filter.useFilter(data, uselessFilter);
        ntbFilter.setInputFormat(data);
        data = Filter.useFilter(data, ntbFilter);
    }

    private void createNames() {
        titles = new String[data.numClasses()];
        titles[0] = ATTR_TEXT;
        for (int k = 1; k < data.numClasses(); k++) {
            titles[k] = String.format(CLASS_FORMAT, k - 1);
        }
    }

}
