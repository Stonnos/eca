package eca.gui.actions;

import eca.core.model.ClassificationModel;
import eca.data.file.resource.DataResource;

/**
 * Classifier model loader.
 *
 * @author Roman Batygin
 */
public class ClassifierModelLoader extends AbstractModelLoader<ClassificationModel> {

    public ClassifierModelLoader(DataResource<?> dataResource) {
        super(dataResource, ClassificationModel.class);
    }
}
