/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.swing.table.AbstractTableModel;
import eca.gui.text.NumericFormat;
import eca.regression.Logistic;
import weka.core.Instances;
import weka.filters.Filter;

/**
 *
 * @author Roman93
 */
public class ROCThresholdTableModel extends AbstractTableModel {

    private String[] titles;
    private final DecimalFormat format = NumericFormat.getInstance();
    private final Instances data;
    private final String className;

    public ROCThresholdTableModel(Instances data, int digits, String className) {
        this.data = data;
        //System.out.println(getData);
        this.className = className;
        //------------------------------------
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
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void createNames() {
        titles = new String[3];
        titles[0] = "Порог для класса (" + className + ")";
        titles[1] = "100 - Специфичность";
        titles[2] = "Чувствительность";
    }
}
