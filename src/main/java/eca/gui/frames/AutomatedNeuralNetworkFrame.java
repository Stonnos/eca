/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import javax.swing.JFrame;
import eca.experiment.AbstractExperiment;
import eca.gui.dialogs.NumberFormatDialog;

/**
 *
 * @author Roman93
 */
public class AutomatedNeuralNetworkFrame extends ExperimentFrame {

    public AutomatedNeuralNetworkFrame(AbstractExperiment experiment, JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle("Автоматическое построение нейронных сетей");
    }

    @Override
    protected void setOptions() {
        NumberFormatDialog dialog =
                new NumberFormatDialog(this, "Настройки", "Количество сетей:",
                        getExperiment().getNumIterations(), 10, 10000);
        dialog.setVisible(true);
        if (dialog.dialogResult()) {
            getExperiment().setNumIterations(dialog.getValue());
        }
    }

}
