/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.MissingCellRenderer;
import eca.gui.tables.models.ResultInstancesTableModel;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class ResultInstancesTable extends JDataTableBase {

    public ResultInstancesTable(Instances data) {
        super(new ResultInstancesTableModel(data));
        MissingCellRenderer renderer = new MissingCellRenderer();
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

}
