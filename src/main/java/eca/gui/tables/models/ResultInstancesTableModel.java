/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables.models;

import weka.core.Instances;

/**
 * @author Roman93
 */
public class ResultInstancesTableModel extends InstancesTableModel {

    public ResultInstancesTableModel(Instances data) {
        super(data);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
