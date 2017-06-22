package eca.gui.actions;

import eca.generators.DataGenerator;
import weka.core.Instances;

/**
 * @author Roman Batygin
 */

public class DataGeneratorLoader implements CallbackAction {

    private DataGenerator dataGenerator;

    private Instances result;

    public DataGeneratorLoader(DataGenerator dataGenerator) {
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
