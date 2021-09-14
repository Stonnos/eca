package eca.gui.actions;

import eca.core.ModelSerializationHelper;
import eca.data.file.resource.DataResource;
import eca.dataminer.AbstractExperiment;

/**
 * Experiment loader.
 *
 * @author Roman Batygin
 */
public class ExperimentLoader extends AbstractCallback<AbstractExperiment<?>> {

    private final DataResource<?> dataResource;

    public ExperimentLoader(DataResource<?> dataResource) {
        this.dataResource = dataResource;
    }

    @Override
    protected AbstractExperiment<?> performAndGetResult() throws Exception {
        return ModelSerializationHelper.deserialize(dataResource, AbstractExperiment.class);
    }
}
