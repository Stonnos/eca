package eca.gui.actions;

import eca.generators.DataGenerator;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */

public class DataGeneratorCallback extends AbstractCallback<Instances> {

    private DataGenerator dataGenerator;

    public DataGeneratorCallback(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void apply() {
        result = dataGenerator.generate();
    }
}