package eca.gui.frames;

import eca.dataminer.AutomatedDecisionTree;
import eca.gui.dialogs.SpinnerDialog;

import javax.swing.*;

import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_DECISION_TREE;

/**
 * @author Roman Batygin
 */
public class AutomatedDecisionTreeFrame extends ExperimentFrame<AutomatedDecisionTree> {

    private static final String OPTIONS_TITLE = "Настройки";
    private static final String EXPERIMENTS_NUMBER_TITLE = "Число экспериментов:";
    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 1000000;

    public AutomatedDecisionTreeFrame(AutomatedDecisionTree experiment, JFrame parent, int digits) {
        super(AutomatedDecisionTree.class, experiment, parent, digits);
        this.setTitle(DATA_MINER_DECISION_TREE);
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
        dialog.dispose();
    }

}
