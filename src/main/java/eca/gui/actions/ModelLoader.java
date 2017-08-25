/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.core.converters.ModelConverter;
import eca.model.ModelDescriptor;

import java.io.File;

/**
 * @author Рома
 */
public class ModelLoader implements CallbackAction {

    private ModelDescriptor model;
    private final File file;

    public ModelLoader(File file) {
        this.file = file;
    }

    public ModelDescriptor model() {
        return model;
    }

    @Override
    public void apply() throws Exception {
        model = (ModelDescriptor) ModelConverter.loadModel(file);
    }
}
