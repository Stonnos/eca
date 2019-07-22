/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.dataminer.AbstractExperiment;
import eca.dataminer.AutomatedStacking;
import eca.ensemble.ClassifiersSet;
import eca.gui.dialogs.StackingOptionsDialog;

import javax.swing.*;

/**
 * @author Roman Batygin
 */
public class AutomatedStackingFrame extends ExperimentFrame {

    private static final String OPTIONS_TITLE = "Настройка параметров";

    public AutomatedStackingFrame(String title, AbstractExperiment experiment, JFrame parent, int digits) {
        super(experiment, parent, digits);
        this.setTitle(title);
    }

    @Override
    protected void setOptions() {
        AutomatedStacking exp = (AutomatedStacking) this.getExperiment();
        StackingOptionsDialog options
                = new StackingOptionsDialog(this, OPTIONS_TITLE,
                exp.getClassifier(), exp.getData(), getDigits());
        options.setMetaClassifierSelectionEnabled(false);
        try {
            options.addClassifiers(new ClassifiersSet(exp.getClassifier().getClassifiers()));
            options.showDialog();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    null, JOptionPane.WARNING_MESSAGE);
        }
    }

}
