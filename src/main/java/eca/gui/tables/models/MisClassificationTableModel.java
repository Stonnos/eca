/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.core.evaluation.Evaluation;

import javax.swing.table.AbstractTableModel;

/**
 * @author Рома
 */
public class MisClassificationTableModel extends AbstractTableModel {

    private static final String ACTUAL_VALUE_TEXT = "Реальное";
    private static final String PREDICTED_VALUE_FORMAT = "%d (Прогнозное)";
    private Evaluation ev;
    private String[] titles;
    private double[][] values;

    public MisClassificationTableModel(Evaluation ev) throws Exception {
        this.ev = ev;
        this.makeTitles();
        this.makeMatrix();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return values.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? row : (int) values[row][column - 1];
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void makeTitles() {
        titles = new String[ev.getData().numClasses() + 1];
        titles[0] = ACTUAL_VALUE_TEXT;
        for (int i = 1; i < titles.length; i++) {
            titles[i] = String.format(PREDICTED_VALUE_FORMAT, i - 1);
        }
    }

    private void makeMatrix() throws Exception {
        values = ev.confusionMatrix();
    }

}
