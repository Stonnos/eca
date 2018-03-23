/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.gui.dialogs.SpinnerDialog;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
public class AutomatedNeuralNetworkFrame extends ExperimentFrame {

    private static final String TITLE_TEXT = "Автоматическое построение нейронных сетей";
    private static final String OPTIONS_TITLE = "Настройки";
    private static final String NETWORKS_NUMBER_TITLE = "Количество сетей:";
    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 10000;

    public AutomatedNeuralNetworkFrame(AbstractExperiment experiment, JFrame parent, int digits) throws Exception {
        super(experiment, parent, digits);
        this.setTitle(TITLE_TEXT);
    }

    @Override
    protected void setOptions() {
        SpinnerDialog dialog =
                new SpinnerDialog(this, OPTIONS_TITLE, NETWORKS_NUMBER_TITLE,
                        getExperiment().getNumIterations(), MIN_ITERATIONS, MAX_ITERATIONS);
        dialog.setVisible(true);
        if (dialog.dialogResult()) {
            getExperiment().setNumIterations(dialog.getValue());
        }
    }

}
