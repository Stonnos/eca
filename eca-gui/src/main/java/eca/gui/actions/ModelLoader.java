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
public class ModelLoader extends AbstractCallback<ClassificationModel> {

    private final File file;

    public ModelLoader(File file) {
        this.file = file;
    }

    @Override
    public void apply() throws Exception {
        result = (ClassificationModel) ModelConverter.loadModel(file);
    }
}
