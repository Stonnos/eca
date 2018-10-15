package eca.gui.tables;

import eca.gui.tables.models.ContingencyTableModel;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */
public class ContingencyJTable extends JDataTableBase {

    public ContingencyJTable(Attribute rowAttribute, Attribute colAttribute, double[][] matrix) {
        super(new ContingencyTableModel(rowAttribute, colAttribute, matrix));
        this.setAutoResizeOff(false);
    }
}
