package eca.gui.tables;

import eca.gui.tables.models.ContingencyTableModel;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */
public class ContingencyJTable extends JDataTableBase {

    public ContingencyJTable(Attribute attributeX, Attribute attributeY, double[][] matrix) {
        super(new ContingencyTableModel(attributeX, attributeY, matrix));
        this.setAutoResizeOff(false);
    }
}
