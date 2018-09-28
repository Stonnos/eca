/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.data.db.ConnectionDescriptor;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.panels.DatabaseConnectionPanel;
import eca.gui.text.LengthDocument;
import eca.gui.validators.TextFieldInputVerifier;

import javax.swing.*;
import java.awt.*;

/**
 * Database saver dialog.
 *
 * @author Roman Batygin
 */
public class DatabaseSaverDialog extends JDialog {

    private static final String TITLE_TEXT = "Сохранение данных в БД";
    private static final String ADDITIONAL_OPTIONS_TEXT = "Дополнительные настройки";
    private static final int FIELD_LENGTH = 255;
    private static final int TEXT_LENGTH = 20;
    private static final String OVERWRITE_TABLE_TEXT = "Удалить существубщую таблицу";
    private static final String TABLE_NAME_TEXT = "Имя таблицы:";
    private static final String START_SYMBOL_IN_TABLE_NAME_FORMAT = "^[a-zA-Z]+$";
    private static final String TABLE_NAME_REGEX = "^[0-9_a-zA-Z]+$";
    private static final String ERROR_FORMAT_TEXT = "Неправильный формат";
    private static final String INVALID_TABLE_NAME_FORMAT_ERROR_TEXT =
            "Имя таблицы должно состоять только из символов латинского\nалфавита, цифр и знаков подчеркивания!";
    private static final String INVALID_TABLE_NAME_FIRST_SYMBOL_ERROR_TEXT =
            "Имя таблицы должно начинаться с латинского символа!";

    private JTextField tableNameField;
    private JCheckBox overwriteCheckbox;

    private DatabaseConnectionPanel databaseConnectionPanel;

    private boolean dialogResult;

    public DatabaseSaverDialog(Frame parent) {
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
        JPanel additionalOptionsPanel = createAdditionalPanel();
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();
        cancelButton.addActionListener(evt -> {
            dialogResult = false;
            setVisible(false);
        });
        okButton.addActionListener(evt -> {
            JTextField firstEmptyField = getFirstEmptyField();
            if (firstEmptyField != null) {
                GuiUtils.showErrorMessageAndRequestFocusOn(DatabaseSaverDialog.this, firstEmptyField);
            } else if (!tableNameField.getText().matches(TABLE_NAME_REGEX)) {
                JOptionPane.showMessageDialog(DatabaseSaverDialog.this, INVALID_TABLE_NAME_FORMAT_ERROR_TEXT,
                        ERROR_FORMAT_TEXT, JOptionPane.WARNING_MESSAGE);
                tableNameField.requestFocusInWindow();
            } else if (!String.valueOf(tableNameField.getText().charAt(0)).matches(START_SYMBOL_IN_TABLE_NAME_FORMAT)) {
                JOptionPane.showMessageDialog(DatabaseSaverDialog.this, INVALID_TABLE_NAME_FIRST_SYMBOL_ERROR_TEXT,
                        ERROR_FORMAT_TEXT, JOptionPane.WARNING_MESSAGE);
                tableNameField.requestFocusInWindow();
            } else {
                dialogResult = true;
                setVisible(false);
            }
        });
        this.add(databaseConnectionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(additionalOptionsPanel, new GridBagConstraints(0, 1, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        this.getRootPane().setDefaultButton(okButton);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public ConnectionDescriptor getConnectionDescriptor() {
        return databaseConnectionPanel.getConnectionDescriptor();
    }

    public void setTableName(String tableName) {
        tableNameField.setText(tableName);
    }

    public String getTableName() {
        return tableNameField.getText();
    }

    public boolean isOverwriteTable() {
        return overwriteCheckbox.isSelected();
    }

    private JPanel createAdditionalPanel() {
        JPanel additionalOptionsPanel = new JPanel(new GridBagLayout());
        additionalOptionsPanel.setBorder(PanelBorderUtils.createTitledBorder(ADDITIONAL_OPTIONS_TEXT));
        tableNameField = new JTextField(TEXT_LENGTH);
        tableNameField.setDocument(new LengthDocument(FIELD_LENGTH));
        tableNameField.setInputVerifier(new TextFieldInputVerifier());
        overwriteCheckbox = new JCheckBox(OVERWRITE_TABLE_TEXT);
        additionalOptionsPanel.add(new JLabel(TABLE_NAME_TEXT),
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(10, 10, 10, 10), 0, 0));
        additionalOptionsPanel.add(tableNameField, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        additionalOptionsPanel.add(overwriteCheckbox, new GridBagConstraints(0, 2, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        return additionalOptionsPanel;
    }

    private JTextField getFirstEmptyField() {
        JTextField field = databaseConnectionPanel.getFirstEmptyField();
        if (field == null && GuiUtils.isEmpty(tableNameField)) {
            return tableNameField;
        }
        return field;
    }

}
