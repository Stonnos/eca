package eca.gui.tables;

import eca.gui.Cleanable;
import eca.gui.tables.models.ContingencyTableModel;
import weka.core.Attribute;

/**
 * @author Roman Batygin
 */
public class ContingencyJTable extends JDataTableBase implements Cleanable {

    public ContingencyJTable(Attribute rowAttribute, Attribute colAttribute, double[][] matrix) {
        super(new ContingencyTableModel(rowAttribute, colAttribute, matrix));
        this.setAutoResizeOff(false);
    }

    @Override
    public void clear() {
        ContingencyTableModel tableModel = (ContingencyTableModel) getModel();
        tableModel.clear();
    }
}
