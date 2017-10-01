package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.gui.dialogs.SpinnerDialog;

import javax.swing.*;

/**
 * @author Roman Batygin
 */

public class AutomatedKNearestNeighboursFrame extends ExperimentFrame {

    private static final String TITLE = "Автоматическое построение KNN";
    private static final String OPTIONS_TITLE = "Настройки";
    private static final String EXPERIMENTS_NUMBER_TITLE = "Число экспериментов:";
    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 1000000;

    public AutomatedKNearestNeighboursFrame(AbstractExperiment experiment, JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(TITLE);
    }

    @Override
    protected void setOptions() {
        SpinnerDialog dialog =
                new SpinnerDialog(this, OPTIONS_TITLE, EXPERIMENTS_NUMBER_TITLE,
                        getExperiment().getNumIterations(), MIN_ITERATIONS, MAX_ITERATIONS);
        dialog.setVisible(true);
        if (dialog.dialogResult()) {
            getExperiment().setNumIterations(dialog.getValue());
        }
    }

}
