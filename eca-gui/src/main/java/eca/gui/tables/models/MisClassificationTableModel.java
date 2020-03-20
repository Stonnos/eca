package eca.gui.tables.models;

import eca.core.evaluation.Evaluation;
import weka.core.Attribute;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */
public class MisClassificationTableModel extends AbstractTableModel {

    private static final String ACTUAL_VALUE_TEXT = "Реальное";
    private static final String PREDICTED_VALUE_FORMAT = "%s (Прогнозное)";
    private Evaluation evaluation;
    private String[] titles;
    private double[][] values;

    public MisClassificationTableModel(Evaluation evaluation) {
        this.evaluation = evaluation;
        this.createTitles();
        this.createConfusionMatrix();
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
        Attribute classAttribute = evaluation.getData().classAttribute();
        return column == 0 ? classAttribute.value(row) : (int) values[row][column - 1];
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void createTitles() {
        titles = new String[evaluation.getData().numClasses() + 1];
        titles[0] = ACTUAL_VALUE_TEXT;
        Attribute classAttribute = evaluation.getData().classAttribute();
        for (int i = 1; i < titles.length; i++) {
            titles[i] = String.format(PREDICTED_VALUE_FORMAT, classAttribute.value(i - 1));
        }
    }

    private void createConfusionMatrix() {
        values = evaluation.confusionMatrix();
    }

}
