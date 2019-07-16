/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.data.file.FileDataLoader;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class InstancesLoader extends AbstractCallback<Instances> {

    private final FileDataLoader dataLoader;

    public InstancesLoader(FileDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    protected Instances performAndGetResult() throws Exception {
        return dataLoader.loadInstances();
    }
}
