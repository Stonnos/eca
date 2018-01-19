/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.regression.Logistic;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
public class LogisticOptionsDialogBase extends BaseOptionsDialog<Logistic> {

    private static final String OPTIONS_MESSAGE = "Параметры логистической регрессии";

    private static final String NUM_ITS_MESSAGE =
            "<html><body>Максимальное число итераций для поиска<br>минимума функции -Log(Likelihood):</body></html>";

    private static final String OPT_METHOD_MESSAGE = "Метод поиска минимума";

    private static final String[] OPT_METHODS = {"Квазиньютоновкий метод", "Метод сопряженных градиентов"};


    private JTextField numItsTextField;

    private JRadioButton newtonRadioButton;
    private JRadioButton gradientRadioButton;

    public LogisticOptionsDialogBase(Window parent, String title,
                                     Logistic model, Instances data) {
        super(parent, title, model, data);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //------------------------------------
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(OPTIONS_MESSAGE));
        numItsTextField = new JTextField(TEXT_FIELD_LENGTH);
        numItsTextField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        numItsTextField.setInputVerifier(new TextFieldInputVerifier());
        //------------------------------------
        ButtonGroup group = new ButtonGroup();
        newtonRadioButton = new JRadioButton(OPT_METHODS[0]);
        gradientRadioButton = new JRadioButton(OPT_METHODS[1]);
        group.add(newtonRadioButton);
        group.add(gradientRadioButton);
        //------------------------------------
        JLabel label = new JLabel(NUM_ITS_MESSAGE);
        optionPanel.add(label,
                new GridBagConstraints(0, 0, 1, 1, 0, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numItsTextField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        JLabel methodLabel = new JLabel(OPT_METHOD_MESSAGE);
        methodLabel.setFont(new Font("Arial", 1, 13));
        //-----------------------------------------------------------
        optionPanel.add(methodLabel,
                new GridBagConstraints(0, 1, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(newtonRadioButton, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        optionPanel.add(gradientRadioButton, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 0), 0, 0));
        //------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (GuiUtils.isEmpty(numItsTextField)) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(LogisticOptionsDialogBase.this, numItsTextField);
                } else {
                    classifier.setMaxIts(Integer.parseInt(numItsTextField.getText().trim()));
                    classifier.setUseConjugateGradientDescent(gradientRadioButton.isSelected());
                    dialogResult = true;
                    setVisible(false);
                }
            }
        });
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
        numItsTextField.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        this.setOptions();
        super.showDialog();
        numItsTextField.requestFocusInWindow();
    }

    private void setOptions() {
        numItsTextField.setText(String.valueOf(classifier.getMaxIts()));
        if (classifier.getUseConjugateGradientDescent()) {
            gradientRadioButton.setSelected(true);
        } else {
            newtonRadioButton.setSelected(true);
        }
    }

}
