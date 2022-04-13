package eca.gui.frames;

import eca.dataminer.AutomatedKNearestNeighbours;
import eca.gui.dialogs.SpinnerDialog;

import javax.swing.*;

import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_KNN;

/**
 * @author Roman Batygin
 */
public class AutomatedKNearestNeighboursFrame extends ExperimentFrame<AutomatedKNearestNeighbours> {

    private static final String OPTIONS_TITLE = "Настройки";
    private static final String EXPERIMENTS_NUMBER_TITLE = "Число экспериментов:";
    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 1000000;

    public AutomatedKNearestNeighboursFrame(AutomatedKNearestNeighbours experiment, JFrame parent, int digits) {
        super(AutomatedKNearestNeighbours.class, experiment, parent, digits);
        this.setTitle(DATA_MINER_KNN);
    }

    @Override
    protected void initializeExperimentOptions() {
        SpinnerDialog dialog =
                new SpinnerDialog(this, OPTIONS_TITLE, EXPERIMENTS_NUMBER_TITLE,
                        getExperiment().getNumIterations(), MIN_ITERATIONS, MAX_ITERATIONS);
        dialog.setVisible(true);
        if (dialog.dialogResult()) {
            getExperiment().setNumIterations(dialog.getValue());
        }
    }

}
