/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dialog frame for setting spinner data.
 *
 * @author Roman Batygin
 */
public class SpinnerDialog extends JDialog {

    private final JSpinner spinner = new JSpinner();

    private boolean dialogResult;

    public SpinnerDialog(Window window, String title1, String title2, int digits, int min, int max) {
        super(window, title1);
        this.setModal(true);
        this.setResizable(false);
        this.setLayout(new GridBagLayout());
        this.createGUI(title2, digits, min, max);
        this.pack();
        this.setLocationRelativeTo(window);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public int getValue() {
        return Integer.valueOf(spinner.getValue().toString());
    }

    private void createGUI(String title2, int digits, int min, int max) {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.add(new JLabel(title2),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(spinner, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-------------------------------------------------------
        spinner.setModel(new SpinnerNumberModel(digits, min, max, 1));
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
                dialogResult = true;
                setVisible(false);
            }
        });
        //------------------------------------
        this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

}
