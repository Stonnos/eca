package eca.gui.actions;

import eca.data.file.resource.DataResource;
import eca.dataminer.AbstractExperiment;

/**
 * Experiment model loader.
 *
 * @author Roman Batygin
 */
public class ExperimentLoader extends AbstractModelLoader<AbstractExperiment> {

    public ExperimentLoader(DataResource<?> dataResource) {
        super(dataResource, AbstractExperiment.class);
    }
}
