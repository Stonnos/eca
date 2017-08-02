package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */

public class NumericAttributeTableModel extends AttributeTableModel {

    private static final int STATISTICS_COUNT = 5;

    private final String[] title = {"Статистика", "Значение"};

    public NumericAttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        super(attribute, attributeStatistics);

        statistica = new Object[STATISTICS_COUNT][getColumnCount()];
        int current = 0;
        statistica[current][0] = "Минимальное значение";
        statistica[current++][1] = attributeStatistics.getMin(attribute);

        statistica[current][0] = "Максимальное значение";
        statistica[current++][1] = attributeStatistics.getMax(attribute);

        statistica[current][0] = "Математическое ожидание";
        statistica[current++][1] = attributeStatistics.meanOrMode(attribute);

        statistica[current][0] = "Дисперсия";
        statistica[current++][1] = attributeStatistics.variance(attribute);

        statistica[current][0] = "Среднеквадратическое отклонение";
        statistica[current++][1] = attributeStatistics.stdDev(attribute);
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