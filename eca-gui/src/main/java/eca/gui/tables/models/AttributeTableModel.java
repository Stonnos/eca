package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

import javax.swing.table.AbstractTableModel;

/**
 * @author Roman Batygin
 */

public abstract class AttributeTableModel extends AbstractTableModel {

    protected Object[][] statistics;

    protected Attribute attribute;
    protected AttributeStatistics attributeStatistics;

    protected AttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        this.attribute = attribute;
        this.attributeStatistics = attributeStatistics;
    }


    @Override
    public int getRowCount() {
        return statistics.length;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return statistics[row][column];
    }

}
