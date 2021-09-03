package eca.gui.frames;

import eca.dataminer.AutomatedRandomForests;
import eca.gui.dialogs.AutomatedRandomForestsOptionsDialog;

import javax.swing.*;

/**
 * Implements frame for automatic selection of optimal options for Random forests algorithm hierarchy.
 *
 * @author Roman Batygin
 */
public class AutomatedRandomForestsFrame extends ExperimentFrame<AutomatedRandomForests> {

    private static final String TITLE = "Автоматическое построение: случайные леса";

    public AutomatedRandomForestsFrame(AutomatedRandomForests experiment, JFrame parent, int digits) {
        super(AutomatedRandomForests.class, experiment, parent, digits);
        this.setTitle(TITLE);
    }

    @Override
    protected void initializeExperimentOptions() {
        AutomatedRandomForests automatedRandomForests = this.getExperiment();
        AutomatedRandomForestsOptionsDialog optionsDialog = new AutomatedRandomForestsOptionsDialog(this,
                automatedRandomForests.getNumIterations(), automatedRandomForests.getNumThreads());
        optionsDialog.setVisible(true);
        if (optionsDialog.dialogResult()) {
            automatedRandomForests.setNumIterations(optionsDialog.getNumIterations());
            automatedRandomForests.setNumThreads(optionsDialog.getNumThreads());
        }
    }

}
