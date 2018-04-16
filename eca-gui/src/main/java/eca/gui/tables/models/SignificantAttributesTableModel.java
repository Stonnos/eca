/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.roc.AttributesSelection;
import eca.text.NumericFormatFactory;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class SignificantAttributesTableModel extends AbstractTableModel {

    private static final String ATTR_TEXT = "Атрибут";
    private static final String AVG_AUC_TEXT = "Avg. AUC";
    private static final String AUC_FORMAT = "AUC (Класс %d)";
    private final AttributesSelection attributesSelection;
    private String[] titles;
    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    public SignificantAttributesTableModel(AttributesSelection attributesSelection, int digits) {
        this.attributesSelection = attributesSelection;
        this.createNames();
        this.decimalFormat.setMaximumFractionDigits(digits);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return attributesSelection.underROCValues().length - 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        int i = row >= attributesSelection.data().classIndex() ? row + 1 : row;
        if (column == 0) {
            return attributesSelection.data().attribute(i).name();
        } else if (column == getColumnCount() - 1) {
            return decimalFormat.format(attributesSelection.underROCAverageValues()[i]);
        } else {
            return decimalFormat.format(attributesSelection.underROCValues()[i][column - 1]);
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }


    private void createNames() {
        titles = new String[attributesSelection.data().numClasses() + 2];
        titles[0] = ATTR_TEXT;
        for (int k = 0; k < attributesSelection.data().numClasses(); k++) {
            titles[k + 1] = String.format(AUC_FORMAT, k);
        }
        titles[titles.length - 1] = AVG_AUC_TEXT;
    }
}
