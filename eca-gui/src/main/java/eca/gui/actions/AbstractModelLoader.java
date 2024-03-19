package eca.gui.actions;

import eca.core.ModelSerializationHelper;
import eca.data.file.resource.DataResource;

/**
 * Abstract model loader.
 *
 * @author Roman Batygin
 */
public abstract class AbstractModelLoader<T> extends AbstractCallback<T> {

    private final DataResource<?> dataResource;
    private final Class<T> modelClass;

    protected AbstractModelLoader(DataResource<?> dataResource, Class<T> modelClass) {
        this.dataResource = dataResource;
        this.modelClass = modelClass;
    }

    @Override
    protected T performAndGetResult() throws Exception {
       return ModelSerializationHelper.deserialize(dataResource, modelClass);
    }
}
