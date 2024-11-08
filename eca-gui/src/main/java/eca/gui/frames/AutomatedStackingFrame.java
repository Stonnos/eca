package eca.gui.frames;

import eca.dataminer.AutomatedStacking;
import eca.ensemble.ClassifiersSet;
import eca.gui.dialogs.StackingOptionsDialog;

import javax.swing.*;

import static eca.gui.GuiUtils.showFormattedErrorMessageDialog;
import static eca.gui.service.ExperimentNamesFactory.DATA_MINER_STACKING;

/**
 * @author Roman Batygin
 */
public class AutomatedStackingFrame extends ExperimentFrame<AutomatedStacking> {

    private static final String OPTIONS_TITLE = "Настройка параметров";

    public AutomatedStackingFrame(AutomatedStacking experiment, JFrame parent, int digits) {
        super(AutomatedStacking.class, experiment, parent, digits);
        this.setTitle(DATA_MINER_STACKING);
    }

    @Override
    protected void initializeExperimentOptions() {
        AutomatedStacking automatedStacking = this.getExperiment();
        StackingOptionsDialog options =
                new StackingOptionsDialog(this, OPTIONS_TITLE, automatedStacking.getClassifier(),
                        automatedStacking.getData(), getDigits());
        options.setMetaClassifierSelectionEnabled(false);
        try {
            options.addClassifiers(new ClassifiersSet(automatedStacking.getClassifier().getClassifiers()));
            options.showDialog();
            options.dispose();
        } catch (Exception e) {
            showFormattedErrorMessageDialog(this, e.getMessage());
        }
    }

}
