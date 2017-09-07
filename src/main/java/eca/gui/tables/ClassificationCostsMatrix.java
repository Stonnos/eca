/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.gui.tables.models.ClassificationCostsTableModel;

import java.text.DecimalFormat;

/**
 * @author Рома
 */
public class ClassificationCostsMatrix extends JDataTableBase {

    public ClassificationCostsMatrix(Evaluation ev, int digits) {
        super(new ClassificationCostsTableModel(ev, digits));
        this.setAutoResizeOff(false);
    }


    public DecimalFormat getFormat() {
        return ((ClassificationCostsTableModel) this.getModel()).getFormat();
    }

}
