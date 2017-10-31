package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */

public class NumericAttributeTableModel extends AttributeTableModel {

    private static final int STATISTICS_COUNT = 5;
    private static final String MIN_VALUE_TEXT = "Минимальное значение";
    private static final String MAX_VALUE_TEXT = "Максимальное значение";
    private static final String MEAN_VALUE_TEXT = "Математическое ожидание";
    private static final String VARIANCE_VALUE_TEXT = "Дисперсия";
    private static final String STD_DEV_VALUE_TEXT = "Среднеквадратическое отклонение";

    private final String[] title = {"Статистика", "Значение"};

    public NumericAttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        super(attribute, attributeStatistics);

        statistica = new Object[STATISTICS_COUNT][getColumnCount()];
        int current = 0;
        statistica[current][0] = MIN_VALUE_TEXT;
        statistica[current++][1] = attributeStatistics.getMinAsString(attribute);

        statistica[current][0] = MAX_VALUE_TEXT;
        statistica[current++][1] = attributeStatistics.getMaxAsString(attribute);

        statistica[current][0] = MEAN_VALUE_TEXT;
        statistica[current++][1] = attributeStatistics.meanOrMode(attribute);

        statistica[current][0] = VARIANCE_VALUE_TEXT;
        statistica[current++][1] = attributeStatistics.variance(attribute);

        statistica[current][0] = STD_DEV_VALUE_TEXT;
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