/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.data.db.ConnectionDescriptor;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.panels.DatabaseConnectionPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Database connection dialog.
 *
 * @author Roman Batygin
 */
public class DatabaseConnectionDialog extends JDialog {

    private static final String TITLE_TEXT = "Подключение к базе данных";

    private DatabaseConnectionPanel databaseConnectionPanel;

    private boolean dialogResult;

    public DatabaseConnectionDialog(Frame parent) {
        super(parent, TITLE_TEXT, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.createGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        databaseConnectionPanel.getHostField().requestFocusInWindow();
    }

    private void createGUI() {
        databaseConnectionPanel = new DatabaseConnectionPanel();
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();
        cancelButton.addActionListener(evt -> {
            dialogResult = false;
            setVisible(false);
        });
        okButton.addActionListener(evt -> {
            JTextField field = databaseConnectionPanel.getFirstEmptyField();
            if (field != null) {
                GuiUtils.showErrorMessageAndRequestFocusOn(DatabaseConnectionDialog.this, field);
            } else {
                dialogResult = true;
                setVisible(false);
            }
        });
        this.add(databaseConnectionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public ConnectionDescriptor getConnectionDescriptor() {
        return databaseConnectionPanel.getConnectionDescriptor();
    }

}
