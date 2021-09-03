package eca.gui.actions;

import eca.core.ModelSerializationHelper;
import eca.core.model.ClassificationModel;
import eca.data.file.resource.FileResource;

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
    protected ClassificationModel performAndGetResult() throws Exception {
        return ModelSerializationHelper.deserialize(new FileResource(file), ClassificationModel.class);
    }
}
