/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import eca.core.evaluation.Evaluation;
import eca.gui.text.NumericFormat;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Рома
 */
public class ClassificationCostsTableModel extends AbstractTableModel {

    private static final String NAN = "NaN";
    private final Evaluation ev;
    private final String[] titles = {"Класс", "TPR", "FPR",
            "TNR", "FNR",
            "Полнота", "Точность", "F - мера", "AUC"};
    private Object[][] values;

    private final DecimalFormat format = NumericFormat.getInstance();

    public ClassificationCostsTableModel(Evaluation ev, int digits) {
        this.ev = ev;
        format.setMaximumFractionDigits(digits);
        this.makeMatrix();
    }

    public DecimalFormat getFormat() {
        return format;
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
        return column == 0 ? row : values[row][column - 1];
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void makeMatrix() {
        Instances data = ev.getData();
        values = new Object[data.numClasses()][titles.length - 1];
        for (int i = 0; i < data.numClasses(); i++) {
            values[i][0] = format.format(ev.truePositiveRate(i));
            values[i][1] = format.format(ev.falsePositiveRate(i));
            values[i][2] = format.format(ev.trueNegativeRate(i));
            values[i][3] = format.format(ev.falseNegativeRate(i));
            values[i][4] = format.format(ev.recall(i));
            values[i][5] = format.format(ev.precision(i));
            values[i][6] = format.format(ev.fMeasure(i));
            double auc = ev.areaUnderROC(i);
            values[i][7] = Double.isNaN(auc) ? NAN : format.format(auc);
        }
    }

}
