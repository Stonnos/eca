/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AutomatedRandomForests;
import eca.ensemble.ClassifiersSet;
import eca.gui.ButtonUtils;
import eca.gui.dialogs.AutomatedRandomForestsOptionsDialog;
import eca.gui.dialogs.EnsembleOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements frame for automatic selection of optimal options for Random forests algorithm hierarchy.
 *
 * @author Roman Batygin
 */
public class AutomatedRandomForestsFrame extends ExperimentFrame {

    public AutomatedRandomForestsFrame(String title, AbstractExperiment experiment,
                                       JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(title);
    }

    @Override
    protected void setOptions() {
        AutomatedRandomForests automatedRandomForests = (AutomatedRandomForests) this.getExperiment();
        AutomatedRandomForestsOptionsDialog optionsDialog = new AutomatedRandomForestsOptionsDialog(this,
                automatedRandomForests.getNumIterations(), automatedRandomForests.getNumThreads());
        optionsDialog.setVisible(true);
        if (optionsDialog.dialogResult()) {
            automatedRandomForests.setNumIterations(optionsDialog.getNumIterations());
            automatedRandomForests.setNumThreads(optionsDialog.getNumThreads());
        }
    }

}
