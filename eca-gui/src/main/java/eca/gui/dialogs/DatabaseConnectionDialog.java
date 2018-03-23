/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.db.ConnectionDescriptor;
import eca.db.ConnectionDescriptorBuilder;
import eca.db.ConnectionDescriptorFactory;
import eca.db.DataBaseType;
import eca.db.DataBaseTypeVisitor;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.gui.text.LengthDocument;
import eca.gui.validators.TextFieldInputVerifier;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Roman Batygin
 */
public class DatabaseConnectionDialog extends JDialog {

    private static final int FIELD_LENGTH = 255;
    private static final int TEXT_LENGTH = 20;
    private static final String TITLE_TEXT = "Подключение к базе данных";
    private static final String CONNECTION_PARAMS_TITLE = "Параметры подключения";
    private static final String DB_TYPE_TEXT = "База данных:";
    private static final String HOST_TEXT = "Хост:";
    private static final String PORT_TEXT = "Порт:";
    private static final String DB_NAME_TEXT = "Имя базы данных:";
    private static final String LOGIN_TEXT = "Логин:";
    private static final String PASSWORD_TEXT = "Пароль:";

    private static final DataBaseType DEFAULT_DATA_BASE_TYPE = DataBaseType.POSTGRESQL;

    private JComboBox<String> dataBases;
    private JTextField hostField;
    private JTextField portField;
    private JTextField dataBaseField;
    private JTextField userField;
    private JPasswordField passwordField;

    private boolean dialogResult;

    private final ConnectionDescriptorBuilder connectionDescriptorBuilder = new ConnectionDescriptorBuilder();

    public DatabaseConnectionDialog(Frame parent) {
        super(parent, TITLE_TEXT, true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        this.makeGUI();
        this.pack();
        this.setLocationRelativeTo(parent);
        hostField.requestFocusInWindow();
    }

    private void makeGUI() {
        JPanel optionPanel = new JPanel(new GridBagLayout());
        optionPanel.setBorder(PanelBorderUtils.createTitledBorder(CONNECTION_PARAMS_TITLE));
        dataBases = new JComboBox<>(DataBaseType.getDescriptions());
        hostField = new JTextField(TEXT_LENGTH);
        hostField.setDocument(new LengthDocument(FIELD_LENGTH));
        hostField.setInputVerifier(new TextFieldInputVerifier());
        portField = new JTextField(TEXT_LENGTH);
        portField.setDocument(new IntegerDocument(8));
        portField.setInputVerifier(new TextFieldInputVerifier());
        dataBaseField = new JTextField(TEXT_LENGTH);
        dataBaseField.setDocument(new LengthDocument(FIELD_LENGTH));
        dataBaseField.setInputVerifier(new TextFieldInputVerifier());
        userField = new JTextField(TEXT_LENGTH);
        userField.setDocument(new LengthDocument(FIELD_LENGTH));
        passwordField = new JPasswordField(TEXT_LENGTH);
        passwordField.setDocument(new LengthDocument(FIELD_LENGTH));
        //-------------------------------------------------------
        dataBases.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                DataBaseType dataBaseType = DataBaseType.findByDescription(dataBases.getSelectedItem().toString());

                setOptions(dataBaseType.handle(new DataBaseTypeVisitor<ConnectionDescriptor>() {
                    @Override
                    public ConnectionDescriptor caseMySql() {
                        return ConnectionDescriptorFactory.getMySqlConnectionDescriptor();
                    }

                    @Override
                    public ConnectionDescriptor casePostgreSQL() {
                        return ConnectionDescriptorFactory.getPostgreSQLConnectionDescriptor();
                    }

                    @Override
                    public ConnectionDescriptor caseOracle() {
                        return ConnectionDescriptorFactory.getOracleConnectionDescriptor();
                    }

                    @Override
                    public ConnectionDescriptor caseMSAccess() {
                        return ConnectionDescriptorFactory.getMSAccessConnectionDescriptor();
                    }

                    @Override
                    public ConnectionDescriptor caseMSSQL() {
                        return ConnectionDescriptorFactory.getMSSqlConnectionDescriptor();
                    }

                    @Override
                    public ConnectionDescriptor caseSQLite() {
                        return ConnectionDescriptorFactory.getSqliteConnectionDescriptor();
                    }
                }));
            }
        });
        dataBases.setSelectedItem(DEFAULT_DATA_BASE_TYPE.getDescription());
        //-------------------------------------------------------
        optionPanel.add(new JLabel(DB_TYPE_TEXT),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(dataBases, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(HOST_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(hostField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(PORT_TEXT), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(portField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(DB_NAME_TEXT), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(dataBaseField, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(LOGIN_TEXT), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(userField, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        optionPanel.add(new JLabel(PASSWORD_TEXT), new GridBagConstraints(0, 5, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        optionPanel.add(passwordField, new GridBagConstraints(1, 5, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //------------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();
        //-----------------------------------------------
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
                JTextField field = emptyField();
                if (field != null) {
                    GuiUtils.showErrorMessageAndRequestFocusOn(DatabaseConnectionDialog.this, field);
                } else {
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
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    private JTextField emptyField() {
        if (GuiUtils.isEmpty(hostField)) {
            return hostField;
        } else if (portField.isEnabled() && GuiUtils.isEmpty(portField)) {
            return portField;
        } else if (GuiUtils.isEmpty(dataBaseField)) {
            return dataBaseField;
        } else {
            return null;
        }
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public ConnectionDescriptor getConnectionDescriptor() {
        ConnectionDescriptor connectionDescriptor = DataBaseType.findByDescription(dataBases
                .getSelectedItem().toString()).handle(connectionDescriptorBuilder);
        connectionDescriptor.setHost(hostField.getText().trim());
        if (!connectionDescriptor.getDataBaseType().isEmbedded()) {
            connectionDescriptor.setPort(Integer.valueOf(portField.getText().trim()));
        }
        connectionDescriptor.setDataBaseName(dataBaseField.getText().trim());
        connectionDescriptor.setLogin(userField.getText().trim());
        connectionDescriptor.setPassword(String.valueOf(passwordField.getPassword()));
        return connectionDescriptor;
    }

    public void setOptions(ConnectionDescriptor connectionDescriptor) {
        hostField.setText(connectionDescriptor.getHost());
        portField.setEnabled(!connectionDescriptor.getDataBaseType().isEmbedded());
        portField.setText(connectionDescriptor.getDataBaseType().isEmbedded() ?
                StringUtils.EMPTY : String.valueOf(connectionDescriptor.getPort()));
        dataBaseField.setText(connectionDescriptor.getDataBaseName());
        userField.setText(connectionDescriptor.getLogin());
        passwordField.setText(connectionDescriptor.getPassword());
        hostField.requestFocusInWindow();
    }

}
