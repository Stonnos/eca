/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.data.DataLoader;
import weka.core.Instances;

import java.io.File;

/**
 * @author Roman Batygin
 */
public class InstancesLoader implements CallbackAction {

    private Instances data;
    private final DataLoader dataLoader;
    private final File file;

    public InstancesLoader(DataLoader dataLoader, File file) {
        this.dataLoader = dataLoader;
        this.file = file;
    }

    public Instances data() {
        return data;
    }

    @Override
    public void apply() throws Exception {
        data = dataLoader.getDataSet(file);
    }

}
