package eca.gui.tables.models;

import eca.statistics.AttributeStatistics;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */

public class NumericAttributeTableModel extends AttributeTableModel {

    private static final String[] TITLES = {"Статистика", "Значение"};

    private static final int STATISTICS_COUNT = 5;
    private static final String MIN_VALUE_TEXT = "Минимальное значение";
    private static final String MAX_VALUE_TEXT = "Максимальное значение";
    private static final String MEAN_VALUE_TEXT = "Математическое ожидание";
    private static final String VARIANCE_VALUE_TEXT = "Дисперсия";
    private static final String STD_DEV_VALUE_TEXT = "Среднеквадратическое отклонение";

    public NumericAttributeTableModel(Attribute attribute, AttributeStatistics attributeStatistics) {
        super(attribute, attributeStatistics);

        statistics = new Object[STATISTICS_COUNT][getColumnCount()];
        int current = 0;
        statistics[current][0] = MIN_VALUE_TEXT;
        statistics[current++][1] = attributeStatistics.getMinAsString(attribute);

        statistics[current][0] = MAX_VALUE_TEXT;
        statistics[current++][1] = attributeStatistics.getMaxAsString(attribute);

        statistics[current][0] = MEAN_VALUE_TEXT;
        statistics[current++][1] = attributeStatistics.meanOrMode(attribute);

        statistics[current][0] = VARIANCE_VALUE_TEXT;
        statistics[current++][1] = attributeStatistics.variance(attribute);

        statistics[current][0] = STD_DEV_VALUE_TEXT;
        statistics[current++][1] = attributeStatistics.stdDev(attribute);
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