/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.text.NumericFormatFactory;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class ROCThresholdTableModel extends AbstractTableModel {

    private static final String SPECIFICITY_TEXT = "100 - Специфичность";
    private static final String SENSITIVITY_TEXT = "Чувствительность";
    private static final String CLASS_THRESHOLD_TEXT = "Порог для класса (%s)";
    private String[] titles;
    private final DecimalFormat format = NumericFormatFactory.getInstance();
    private final Instances data;
    private final String className;

    public ROCThresholdTableModel(Instances data, int digits, String className) {
        this.data = data;
        this.className = className;
        this.createNames();
        format.setMaximumFractionDigits(digits);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return data.numInstances();
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return format.format(data.instance(row).value(12));
            case 1:
                return format.format(data.instance(row).value(4) * 100);
            case 2:
                return format.format(data.instance(row).value(5) * 100);
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void createNames() {
        titles = new String[3];
        titles[0] = String.format(CLASS_THRESHOLD_TEXT, className);
        titles[1] = SPECIFICITY_TEXT;
        titles[2] = SENSITIVITY_TEXT;
    }
}
