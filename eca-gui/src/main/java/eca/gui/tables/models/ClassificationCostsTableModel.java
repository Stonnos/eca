package eca.gui.tables.models;

import eca.core.evaluation.Evaluation;
import eca.text.NumericFormatFactory;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;

import javax.swing.table.AbstractTableModel;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class ClassificationCostsTableModel extends AbstractTableModel {

    private static final String NAN = "NaN";
    private static final int TP_IDX = 0;
    private static final int FP_IDX = 1;
    private static final int TN_IDX = 2;
    private static final int FN_IDX = 3;
    private static final int RECALL_IDX = 4;
    private static final int PRECISION_IDX = 5;
    private static final int FM_IDX = 6;
    private static final int AUC_IDX = 7;
    private static final String[] TITLES =
            {"Класс", "TPR", "FPR", "TNR", "FNR", "Полнота", "Точность", "F - мера", "AUC"};
    private Object[][] values;

    private final Evaluation evaluation;
    private final DecimalFormat format = NumericFormatFactory.getInstance();

    public ClassificationCostsTableModel(Evaluation evaluation, int digits) {
        this.evaluation = evaluation;
        format.setMaximumFractionDigits(digits);
        this.createMatrix();
    }

    public DecimalFormat getFormat() {
        return format;
    }

    @Override
    public int getColumnCount() {
        return TITLES.length;
    }

    @Override
    public int getRowCount() {
        return values.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Attribute classAttribute = evaluation.getData().classAttribute();
        return column == 0 ? classAttribute.value(row) : values[row][column - 1];
    }

    @Override
    public String getColumnName(int column) {
        return TITLES[column];
    }

    private void createMatrix() {
        Instances data = evaluation.getData();
        values = new Object[data.numClasses()][TITLES.length - 1];
        for (int i = 0; i < data.numClasses(); i++) {
            values[i][TP_IDX] = format.format(evaluation.truePositiveRate(i));
            values[i][FP_IDX] = format.format(evaluation.falsePositiveRate(i));
            values[i][TN_IDX] = format.format(evaluation.trueNegativeRate(i));
            values[i][FN_IDX] = format.format(evaluation.falseNegativeRate(i));
            values[i][RECALL_IDX] = format.format(evaluation.recall(i));
            values[i][PRECISION_IDX] = format.format(evaluation.precision(i));
            values[i][FM_IDX] = format.format(evaluation.fMeasure(i));
            double auc = evaluation.areaUnderROC(i);
            values[i][AUC_IDX] = Utils.isMissingValue(auc) ? NAN : format.format(auc);
        }
    }

}
