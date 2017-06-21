/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import javax.swing.JDialog;
import java.awt.Window;
import javax.swing.JTextField;
import weka.classifiers.Classifier;
import weka.core.Instances;

/**
 *
 * @author Рома
 * @param <T>
 */
public abstract class BaseOptionsDialog<T extends Classifier> extends JDialog {

    public static final int INT_FIELD_LENGTH = 8;
    public static final int TEXT_FIELD_LENGTH = 8;

    protected T classifier;
    protected Instances data;
    protected boolean dialogResult;

    public BaseOptionsDialog(Window parent, String title, T classifier, Instances data) {
        super(parent, title);
        this.classifier = classifier;
        this.data = data;
        this.setModal(true);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public void showDialog() {
        this.setVisible(true);
    }

    public final T classifier() {
        return classifier;
    }

    public final Instances data() {
        return data;
    }

    protected final boolean isEmpty(JTextField field) {
        return field.getText().isEmpty();
    }
}
