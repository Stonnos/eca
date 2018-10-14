package eca.gui.tables.models;

import weka.core.Attribute;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */
public class ContingencyTableModel extends AbstractTableModel {

    private static final String SUMMARY_TEXT = "Всего";
    private static final String ATTRIBUTES_COLUMN_FORMAT = "%s/%s";

    private String[] titles;
    private Attribute attributeX;
    private Attribute attributeY;
    private double[][] matrix;

    public ContingencyTableModel(Attribute attributeX, Attribute attributeY, double[][] matrix) {
        this.attributeX = attributeX;
        this.attributeY = attributeY;
        this.matrix = matrix;
        this.initTitles();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return attributeX.numValues() + 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return row < getRowCount() - 1 ? attributeX.value(row) : SUMMARY_TEXT;
        } else {
            return matrix[row][column - 1];
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void initTitles() {
        titles = new String[attributeY.numValues() + 1];
        titles[0] = String.format(ATTRIBUTES_COLUMN_FORMAT, attributeX.name(), attributeY.name());
        for (int i = 0; i < attributeY.numValues(); i++) {
            titles[i + 1] = attributeY.value(i);
        }
        titles[attributeY.numValues()] = SUMMARY_TEXT;
    }

}
