package eca.gui.tables;

import eca.gui.Cleanable;
import eca.gui.renderers.MissingCellRenderer;
import eca.gui.tables.models.ResultInstancesTableModel;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class ResultInstancesTable extends JDataTableBase implements Cleanable {

    public ResultInstancesTable(Instances data) {
        super(new ResultInstancesTableModel(data));
        MissingCellRenderer renderer = new MissingCellRenderer();
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    @Override
    public void clear() {
        ResultInstancesTableModel tableModel = (ResultInstancesTableModel) getModel();
        tableModel.clearFully();
    }
}
