/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import javax.swing.JTable;

import eca.gui.tables.models.MisClassificationTableModel;
import weka.core.Instances;
import weka.classifiers.Classifier;
import eca.core.evaluation.Evaluation;

/**
 *
 * @author Рома
 */
public class MisClassificationMatrix extends JDataTableBase {
    
    public MisClassificationMatrix(Instances data, Classifier classifier,
            Evaluation ev) throws Exception {
        super(new MisClassificationTableModel(data, classifier, ev));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }
    
}
