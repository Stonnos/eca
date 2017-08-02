package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */

public class NominalAttributeTableModel extends AttributeTableModel {

    private final String[] title = {"Код", "Значение", "Число объектов"};

    public NominalAttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        super(attribute, attributeStatistics);

        statistica = new Object[attribute.numValues()][getColumnCount()];
        for (int i = 0; i < attribute.numValues(); i++) {
            statistica[i][0] = i;
            statistica[i][1] = attribute.value(i);
            statistica[i][2] = attributeStatistics.getValuesNum(attribute, i);
        }
    }


    @Override
    public int getColumnCount() {
        return title.length;
    }

    @Override
    public String getColumnName(int column) {
        return title[column];
    }

}
