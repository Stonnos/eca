package eca.gui.panels;

import eca.config.ConfigurationService;
import eca.config.DatabaseConfig;
import eca.data.db.ConnectionDescriptor;
import eca.data.db.ConnectionDescriptorBuilder;
import eca.data.db.DataBaseType;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.text.IntegerDocument;
import eca.gui.text.LengthDocument;
import eca.gui.validators.TextFieldInputVerifier;
import eca.util.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static com.google.common.collect.Maps.newEnumMap;

/**
 * Implements database connection panel;
 *
 * @author Roman Batygin
 */
public class DatabaseConnectionPanel extends JPanel {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final ConnectionDescriptorBuilder CONNECTION_DESCRIPTOR_BUILDER = new ConnectionDescriptorBuilder();

    private static final int PORT_LENGTH = 8;
    private static final int FIELD_LENGTH = 255;
    private static final int TEXT_LENGTH = 20;
    private static final String CONNECTION_PARAMS_TITLE = "Параметры подключения";
    private static final String DB_TYPE_TEXT = "База данных:";
    private static final String HOST_TEXT = "Хост:";
    private static final String PORT_TEXT = "Порт:";
    private static final String DB_NAME_TEXT = "Имя базы данных:";
    private static final String LOGIN_TEXT = "Логин:";
    private static final String PASS_TEXT = "Пароль:";

    private static final DataBaseType DEFAULT_DATA_BASE_TYPE = DataBaseType.POSTGRESQL;

    private JComboBox<String> dataBases;
    private JTextField hostField;
    private JTextField portField;
    private JTextField dataBaseField;
    private JTextField userField;
    private JPasswordField passwordField;

    private Map<DataBaseType, ConnectionDescriptor> connectionDescriptorMap = newEnumMap(DataBaseType.class);

    public DatabaseConnectionPanel() {
        super(new GridBagLayout());
        this.init();
    }

    private void init() {
        this.setBorder(PanelBorderUtils.createTitledBorder(CONNECTION_PARAMS_TITLE));
        dataBases = new JComboBox<>(EnumUtils.getDescriptions(DataBaseType.class));
        hostField = new JTextField(TEXT_LENGTH);
        hostField.setDocument(new LengthDocument(FIELD_LENGTH));
        hostField.setInputVerifier(new TextFieldInputVerifier());
        portField = new JTextField(TEXT_LENGTH);
        portField.setDocument(new IntegerDocument(PORT_LENGTH));
        portField.setInputVerifier(new TextFieldInputVerifier());
        dataBaseField = new JTextField(TEXT_LENGTH);
        dataBaseField.setDocument(new LengthDocument(FIELD_LENGTH));
        dataBaseField.setInputVerifier(new TextFieldInputVerifier());
        userField = new JTextField(TEXT_LENGTH);
        userField.setDocument(new LengthDocument(FIELD_LENGTH));
        passwordField = new JPasswordField(TEXT_LENGTH);
        passwordField.setDocument(new LengthDocument(FIELD_LENGTH));
        dataBases.addItemListener(evt -> {
            DataBaseType dataBaseType =
                    EnumUtils.fromDescription(dataBases.getSelectedItem().toString(), DataBaseType.class);
            setConnectionDescriptor(dataBaseType);
        });
        dataBases.setSelectedItem(DEFAULT_DATA_BASE_TYPE.getDescription());
        this.add(new JLabel(DB_TYPE_TEXT),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(dataBases, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 10, 10), 0, 0));
        this.add(new JLabel(HOST_TEXT), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(hostField, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.add(new JLabel(PORT_TEXT), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(portField, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.add(new JLabel(DB_NAME_TEXT), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(dataBaseField, new GridBagConstraints(1, 3, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.add(new JLabel(LOGIN_TEXT), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(userField, new GridBagConstraints(1, 4, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.add(new JLabel(PASS_TEXT), new GridBagConstraints(0, 5, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        this.add(passwordField, new GridBagConstraints(1, 5, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
    }

    public ConnectionDescriptor getConnectionDescriptor() {
        DataBaseType dataBaseType =
                EnumUtils.fromDescription(dataBases.getSelectedItem().toString(), DataBaseType.class);
        ConnectionDescriptor connectionDescriptor = dataBaseType.handle(CONNECTION_DESCRIPTOR_BUILDER);
        connectionDescriptor.setHost(hostField.getText().trim());
        if (!connectionDescriptor.getDataBaseType().isEmbedded()) {
            connectionDescriptor.setPort(Integer.valueOf(portField.getText().trim()));
        }
        connectionDescriptor.setDataBaseName(dataBaseField.getText().trim());
        connectionDescriptor.setLogin(userField.getText().trim());
        connectionDescriptor.setPassword(String.valueOf(passwordField.getPassword()));
        connectionDescriptor.setDriver(CONFIG_SERVICE.getDatabaseConfig(dataBaseType).getDriver());
        return connectionDescriptor;
    }

    public JTextField getHostField() {
        return hostField;
    }

    public JTextField getPortField() {
        return portField;
    }

    public JTextField getDataBaseField() {
        return dataBaseField;
    }

    public JTextField getUserField() {
        return userField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JTextField getFirstEmptyField() {
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

    private void setOptions(ConnectionDescriptor connectionDescriptor) {
        hostField.setText(connectionDescriptor.getHost());
        portField.setEnabled(!connectionDescriptor.getDataBaseType().isEmbedded());
        portField.setText(connectionDescriptor.getDataBaseType().isEmbedded() ?
                StringUtils.EMPTY : String.valueOf(connectionDescriptor.getPort()));
        dataBaseField.setText(connectionDescriptor.getDataBaseName());
        userField.setText(connectionDescriptor.getLogin());
        passwordField.setText(connectionDescriptor.getPassword());
        hostField.requestFocusInWindow();
    }

    private void setConnectionDescriptor(DataBaseType dataBaseType) {
        if (!connectionDescriptorMap.containsKey(dataBaseType)) {
            DatabaseConfig databaseConfig = CONFIG_SERVICE.getDatabaseConfig(dataBaseType);
            ConnectionDescriptor connectionDescriptor = dataBaseType.handle(CONNECTION_DESCRIPTOR_BUILDER);
            connectionDescriptor.setDriver(databaseConfig.getDriver());
            connectionDescriptor.setDataBaseName(databaseConfig.getDataBaseName());
            connectionDescriptor.setHost(databaseConfig.getHost());
            connectionDescriptor.setLogin(databaseConfig.getLogin());
            connectionDescriptor.setPassword(databaseConfig.getPassword());
            if (databaseConfig.getPort() != null) {
                connectionDescriptor.setPort(databaseConfig.getPort());
            }
            connectionDescriptorMap.put(dataBaseType, connectionDescriptor);
        }
        setOptions(connectionDescriptorMap.get(dataBaseType));
    }
}
