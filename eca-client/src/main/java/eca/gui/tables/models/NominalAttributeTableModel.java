package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */

public class NominalAttributeTableModel extends AttributeTableModel {

    private static final String[] TITLES = {"Код", "Значение", "Число объектов"};

    public NominalAttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        super(attribute, attributeStatistics);

        statistics = new Object[attribute.numValues()][getColumnCount()];
        for (int i = 0; i < attribute.numValues(); i++) {
            statistics[i][0] = i;
            statistics[i][1] = attribute.value(i);
            statistics[i][2] = attributeStatistics.getValuesNum(attribute, i);
        }
    }


    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

}
