/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.net.DataLoaderImpl;
import weka.core.Instances;

/**
 * @author Roman93
 */
public class URLLoader implements CallbackAction {

    private Instances data;
    private final DataLoaderImpl loader;

    public URLLoader(DataLoaderImpl loader) {
        this.loader = loader;
    }

    public Instances data() {
        return data;
    }

    @Override
    public void apply() throws Exception {
        data = loader.loadInstances();
    }
}
