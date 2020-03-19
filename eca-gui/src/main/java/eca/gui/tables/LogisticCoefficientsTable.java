package eca.gui.tables;

import eca.gui.tables.models.LogisticCoefficientsTableModel;
import eca.regression.Logistic;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class LogisticCoefficientsTable extends JDataTableBase {

    public LogisticCoefficientsTable(Logistic logistic, Instances data, int digits) throws Exception {
        super(new LogisticCoefficientsTableModel(logistic, data, digits));
    }

    public Logistic getLogistic() {
        return ((LogisticCoefficientsTableModel) getModel()).getLogistic();
    }

    public Instances getInstances() {
        return ((LogisticCoefficientsTableModel) getModel()).getData();
    }
}
