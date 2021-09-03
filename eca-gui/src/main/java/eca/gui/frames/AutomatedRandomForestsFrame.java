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

    public AutomatedRandomForestsFrame(String title, AutomatedRandomForests experiment, JFrame parent, int digits) {
        super(AutomatedRandomForests.class, experiment, parent, digits);
        this.setTitle(title);
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
