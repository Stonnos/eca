package eca.gui.tables.models;

import eca.regression.Logistic;
import eca.text.NumericFormatFactory;
import lombok.Getter;
import weka.core.Instances;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class LogisticCoefficientsTableModel extends AbstractTableModel {

    private static final String INTERCEPT = "Intercept";
    private static final String ATTR_TEXT = "Атрибут";
    private static final String CLASS_FORMAT = "Класс %d";

    @Getter
    private final Logistic logistic;
    @Getter
    private final Instances data;

    private String[] titles;
    private final DecimalFormat format = NumericFormatFactory.getInstance();

    public LogisticCoefficientsTableModel(Logistic logistic, Instances data, int digits) throws Exception {
        this.logistic = logistic;
        this.data = data;
        this.createHeader();
        format.setMaximumFractionDigits(digits);
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public int getRowCount() {
        return logistic.coefficients().length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return column == 0 ? getAttributeName(row) : format.format(logistic.coefficients()[row][column - 1]);
    }

    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    private String getAttributeName(int row) {
        return row == 0 ? INTERCEPT : data.attribute(row - 1).name();
    }

    private void createHeader() {
        titles = new String[data.numClasses()];
        titles[0] = ATTR_TEXT;
        for (int k = 1; k < data.numClasses(); k++) {
            titles[k] = String.format(CLASS_FORMAT, k - 1);
        }
    }
}
