/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.gui.text.NumericFormat;
import eca.roc.AttributesSelection;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Рома
 */
public class SignificantAttributesTableModel extends AbstractTableModel {

    private final AttributesSelection roc;
    private String[] titles;
    private final DecimalFormat format = NumericFormat.getInstance();

    public SignificantAttributesTableModel(AttributesSelection roc, int digits) {
        this.roc = roc;
        this.createNames();
        format.setMaximumFractionDigits(digits);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return roc.underROCValues().length - 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        int i = row >= roc.data().classIndex() ? row + 1 : row;
        if (column == 0) {
            return roc.data().attribute(i).name();
        } else if (column == getColumnCount() - 1) {
            return format.format(roc.underROCAverageValues()[i]);
        } else {
            return format.format(roc.underROCValues()[i][column - 1]);
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }


    private void createNames() {
        titles = new String[roc.data().numClasses() + 2];
        titles[0] = "Атрибут";
        for (int k = 0; k < roc.data().numClasses(); k++) {
            titles[k + 1] = "AUC (Класс " + k + ")";
        }
        titles[titles.length - 1] = "Avg. AUC";
    }
}
