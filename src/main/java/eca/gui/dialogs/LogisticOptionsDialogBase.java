/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.regression.Logistic;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Рома
 */
public class LogisticOptionsDialogBase extends BaseOptionsDialog<Logistic> {

    private static final String optionsMessage = "Параметры логиcтической регрессии";

    private static final String numItsMessage = "<html><body>Максимальное число итераций для"
            + " поиска<br>минимума функции -Log(Likelihood):</body></html>";

    private static final String optMethodMessage = "Метод поиска минимума";

    private static final String[] optMethods = {"Квазиньютоновкий метод", "Метод сопряженных градиентов"};


    private JTextField numItsText;

    private JRadioButton newton, gradient;

    public LogisticOptionsDialogBase(Window parent, String title,
                                     Logistic model, Instances data) {
        super(parent, title, model, data);
        this.data = data;
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        //------------------------------------
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(optionsMessage));
        numItsText = new JTextField(TEXT_FIELD_LENGTH);
        numItsText.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
        //------------------------------------
        ButtonGroup group = new ButtonGroup();
        newton = new JRadioButton(optMethods[0]);
        gradient = new JRadioButton(optMethods[1]);
        group.add(newton);
        group.add(gradient);
        //------------------------------------
        JLabel label = new JLabel(numItsMessage);
        optionPanel.add(label,
                new GridBagConstraints(0, 0, 1, 1, 0, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(numItsText, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        JLabel methodLabel = new JLabel(optMethodMessage);
        methodLabel.setFont(new Font("Arial", 1, 13));
        //-----------------------------------------------------------
        optionPanel.add(methodLabel,
                new GridBagConstraints(0, 1, 2, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(newton, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 5, 0), 0, 0));
        optionPanel.add(gradient, new GridBagConstraints(0, 3, 2, 1, 1, 1,
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
                if (numItsText.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(LogisticOptionsDialogBase.this,
                            "Заполните все поля!",
                            "Ошибка ввода", JOptionPane.WARNING_MESSAGE);
                    numItsText.requestFocusInWindow();
                } else {
                    classifier.setMaxIts(Integer.parseInt(numItsText.getText()));
                    classifier.setUseConjugateGradientDescent(gradient.isSelected());
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
        numItsText.requestFocusInWindow();
    }

    @Override
    public final void showDialog() {
        this.setOptions();
        super.showDialog();
        numItsText.requestFocusInWindow();
    }

    private void setOptions() {
        numItsText.setText(String.valueOf(classifier.getMaxIts()));
        if (classifier.getUseConjugateGradientDescent()) {
            gradient.setSelected(true);
        } else {
            newton.setSelected(true);
        }
    }

}
