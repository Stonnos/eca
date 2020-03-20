package eca.gui.tables;

import eca.core.evaluation.Evaluation;
import eca.gui.tables.models.MisClassificationTableModel;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
public class MisClassificationMatrix extends JDataTableBase {

    public MisClassificationMatrix(Evaluation evaluation)  {
        super(new MisClassificationTableModel(evaluation));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

}
