package eca.gui.frames;

import eca.dataminer.AbstractExperiment;

import javax.swing.*;

/**
 * @author Roman Batygin
 */

public class KNNOptimizerFrame extends ExperimentFrame {

    private static final String TITLE = "Вычисление оптимального числа ближайших соседей";

    public KNNOptimizerFrame(AbstractExperiment experiment, JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(TITLE);
    }

    @Override
    protected void setOptions() {
    }

}
