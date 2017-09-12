/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.gui.dialogs.EnsembleOptionsDialog;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
public class AutomatedHeterogeneousEnsembleFrame extends ExperimentFrame {

    private static final String OPTIONS_TITLE = "Настройки параметров";

    public AutomatedHeterogeneousEnsembleFrame(String title, AbstractExperiment experiment,
                                               JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(title);
    }

    @Override
    protected void setOptions() {
        AutomatedHeterogeneousEnsemble exp = (AutomatedHeterogeneousEnsemble) this.getExperiment();
        EnsembleOptionsDialog options = new EnsembleOptionsDialog(this, OPTIONS_TITLE, exp.getClassifier(),
                exp.getData());
        options.setSampleEnabled(false);
        options.addClassifiers(exp.getClassifier().getClassifiersSet().clone());
        options.showDialog();
    }

}
