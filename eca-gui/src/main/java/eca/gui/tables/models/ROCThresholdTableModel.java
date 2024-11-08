/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.roc.RocCurve;
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
    private static final int THRESHOLD_COLUMN_INDEX = 0;
    private static final int SPECIFICITY_COLUMN_INDEX = 1;
    private static final int SENSITIVITY_COLUMN_INDEX = 2;
    private String[] titles;
    private final DecimalFormat format = NumericFormatFactory.getInstance();
    private Instances data;
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
            case THRESHOLD_COLUMN_INDEX:
                return format.format(data.instance(row).value(RocCurve.THRESHOLD_INDEX));
            case SPECIFICITY_COLUMN_INDEX:
                return format.format(data.instance(row).value(RocCurve.SPECIFICITY_INDEX) * 100);
            case SENSITIVITY_COLUMN_INDEX:
                return format.format(data.instance(row).value(RocCurve.SENSITIVITY_INDEX) * 100);
            default:
                return null;
        }
    }

    public void clear() {
        data.clear();
        data = null;
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void createNames() {
        titles = new String[3];
        titles[THRESHOLD_COLUMN_INDEX] = String.format(CLASS_THRESHOLD_TEXT, className);
        titles[SPECIFICITY_COLUMN_INDEX] = SPECIFICITY_TEXT;
        titles[SENSITIVITY_COLUMN_INDEX] = SENSITIVITY_TEXT;
    }
}
