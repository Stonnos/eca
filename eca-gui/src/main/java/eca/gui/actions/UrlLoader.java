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
public class UrlLoader extends AbstractCallback<Instances> {

    private final FileDataLoader loader;

    public UrlLoader(FileDataLoader loader) {
        this.loader = loader;
    }

    @Override
    public void apply() throws Exception {
        result = loader.loadInstances();
    }
}
