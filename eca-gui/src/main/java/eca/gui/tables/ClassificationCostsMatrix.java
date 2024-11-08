package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.gui.tables.models.ClassificationCostsTableModel;
import eca.model.ReferenceWrapper;

import java.text.DecimalFormat;

/**
 * @author Roman Batygin
 */
public class ClassificationCostsMatrix extends JDataTableBase {

    public ClassificationCostsMatrix(ReferenceWrapper<Evaluation> ev, int digits) {
        super(new ClassificationCostsTableModel(ev, digits));
        this.setAutoResizeOff(false);
    }

    public DecimalFormat getFormat() {
        return ((ClassificationCostsTableModel) this.getModel()).getFormat();
    }

}
