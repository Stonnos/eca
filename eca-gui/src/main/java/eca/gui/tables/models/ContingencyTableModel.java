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
    private Attribute rowAttribute;
    private Attribute colAttribute;
    private double[][] matrix;

    public ContingencyTableModel(Attribute rowAttribute, Attribute colAttribute, double[][] matrix) {
        this.rowAttribute = rowAttribute;
        this.colAttribute = colAttribute;
        this.matrix = matrix;
        this.initTitles();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return rowAttribute.numValues() + 1;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            return row < getRowCount() - 1 ? rowAttribute.value(row) : SUMMARY_TEXT;
        } else {
            return matrix[row][column - 1];
        }
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private void initTitles() {
        titles = new String[colAttribute.numValues() + 1];
        titles[0] = String.format(ATTRIBUTES_COLUMN_FORMAT, rowAttribute.name(), colAttribute.name());
        for (int i = 0; i < colAttribute.numValues(); i++) {
            titles[i + 1] = colAttribute.value(i);
        }
        titles[colAttribute.numValues()] = SUMMARY_TEXT;
    }

}
