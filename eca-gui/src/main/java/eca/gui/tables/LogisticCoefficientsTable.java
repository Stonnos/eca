/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
}
