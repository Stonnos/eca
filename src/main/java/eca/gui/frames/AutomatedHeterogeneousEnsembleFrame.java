/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.dataminer.AbstractExperiment;
import javax.swing.JFrame;
import eca.gui.dialogs.EnsembleOptionsDialog;

/**
 *
 * @author Roman93
 */
public class AutomatedHeterogeneousEnsembleFrame extends ExperimentFrame {

    public AutomatedHeterogeneousEnsembleFrame(String title, AbstractExperiment experiment,
            JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(title);
    }

    @Override
    protected void setOptions() {
        AutomatedHeterogeneousEnsemble exp = (AutomatedHeterogeneousEnsemble) this.getExperiment();
        EnsembleOptionsDialog options = new EnsembleOptionsDialog(this, "Настройки параметров", exp.getClassifier(),
                exp.data());
        options.setSampleEnabled(false);
        options.addClassifiers(exp.getClassifier().getClassifiersSet().clone());       
        options.showDialog();
    }

}
