/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.data.net.UrlDataLoader;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */
public class UrlLoader implements CallbackAction {

    private Instances data;
    private final UrlDataLoader loader;

    public UrlLoader(UrlDataLoader loader) {
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
