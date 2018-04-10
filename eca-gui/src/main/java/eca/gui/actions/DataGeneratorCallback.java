package eca.gui.actions;

import eca.generators.DataGenerator;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */

public class DataGeneratorCallback implements CallbackAction {

    private DataGenerator dataGenerator;

    private Instances result;

    public DataGeneratorCallback(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void apply() {
        result = dataGenerator.generate();
    }

    public Instances getResult() {
        return result;
    }
}
