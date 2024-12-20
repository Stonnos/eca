package eca.gui.frames;

import eca.dataminer.AutomatedNeuralNetwork;
import eca.gui.dialogs.SpinnerDialog;

import javax.swing.*;

import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_NETWORKS;

/**
 * @author Roman Batygin
 */
public class AutomatedNeuralNetworkFrame extends ExperimentFrame<AutomatedNeuralNetwork> {

    private static final String OPTIONS_TITLE = "Настройки";
    private static final String NETWORKS_NUMBER_TITLE = "Количество сетей:";
    private static final int MIN_ITERATIONS = 10;
    private static final int MAX_ITERATIONS = 10000;

    public AutomatedNeuralNetworkFrame(AutomatedNeuralNetwork experiment, JFrame parent, int digits) {
        super(AutomatedNeuralNetwork.class, experiment, parent, digits);
        this.setTitle(DATA_MINER_NETWORKS);
    }

    @Override
    protected void initializeExperimentOptions() {
        SpinnerDialog dialog =
                new SpinnerDialog(this, OPTIONS_TITLE, NETWORKS_NUMBER_TITLE,
                        getExperiment().getNumIterations(), MIN_ITERATIONS, MAX_ITERATIONS);
        dialog.setVisible(true);
        if (dialog.dialogResult()) {
            getExperiment().setNumIterations(dialog.getValue());
        }
        dialog.dispose();
    }

}
