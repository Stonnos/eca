package eca.gui.frames;

import eca.dataminer.AutomatedHeterogeneousEnsemble;
import eca.ensemble.ClassifiersSet;
import eca.gui.dialogs.EnsembleOptionsDialog;

import javax.swing.*;

import static eca.gui.GuiUtils.showFormattedErrorMessageDialog;

/**
 * @author Roman Batygin
 */
public class AutomatedHeterogeneousEnsembleFrame extends ExperimentFrame<AutomatedHeterogeneousEnsemble> {

    private static final String OPTIONS_TITLE = "Настройки параметров";

    public AutomatedHeterogeneousEnsembleFrame(String title, AutomatedHeterogeneousEnsemble experiment, JFrame parent,
                                               int digits) {
        super(AutomatedHeterogeneousEnsemble.class, experiment, parent, digits);
        this.setTitle(title);
    }

    @Override
    protected void initializeExperimentOptions() {
        AutomatedHeterogeneousEnsemble automatedHeterogeneousEnsemble = this.getExperiment();
        EnsembleOptionsDialog options =
                new EnsembleOptionsDialog(this, OPTIONS_TITLE, automatedHeterogeneousEnsemble.getClassifier(),
                        automatedHeterogeneousEnsemble.getData(), getDigits());
        options.setSampleEnabled(false);
        try {
            options.addClassifiers(
                    new ClassifiersSet(automatedHeterogeneousEnsemble.getClassifier().getClassifiersSet()));
            options.showDialog();
        } catch (Exception ex) {
            showFormattedErrorMessageDialog(AutomatedHeterogeneousEnsembleFrame.this, ex.getMessage());
        }
    }

}
