/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.Cleanable;
import eca.gui.tables.models.ROCThresholdTableModel;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class ROCThresholdTable extends JDataTableBase implements Cleanable {

    public ROCThresholdTable(Instances data, int digits, String className) {
        super(new ROCThresholdTableModel(data, digits, className));
    }

    @Override
    public void clear() {
        ROCThresholdTableModel tableModel = (ROCThresholdTableModel) getModel();
        tableModel.clear();
    }
}