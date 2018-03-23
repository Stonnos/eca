/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.actions;

import eca.converters.ModelConverter;
import eca.converters.model.ClassificationModel;

import java.io.File;

/**
 * @author Roman Batygin
 */
public class ModelLoader implements CallbackAction {

    private ClassificationModel model;
    private final File file;

    public ModelLoader(File file) {
        this.file = file;
    }

    public ClassificationModel model() {
        return model;
    }

    @Override
    public void apply() throws Exception {
        model = (ClassificationModel) ModelConverter.loadModel(file);
    }
}
