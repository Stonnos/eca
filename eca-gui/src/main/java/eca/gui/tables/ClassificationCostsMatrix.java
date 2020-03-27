package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.gui.tables.models.ClassificationCostsTableModel;

import javax.swing.*;
import java.text.DecimalFormat;

/**
 * @author Roman Batygin
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
