package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.gui.ButtonUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.tables.EcaServicePropertiesTable;
import eca.gui.tables.models.EcaServiceOptionsTableModel;
import eca.util.Entry;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class EcaServiceOptionsDialog extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String TITLE_TEXT = "Настройки сервиса ECA";
    private static final String EMPTY_PROPERTY_ERROR_FORMAT = "Укажите значение свойства '%s'";
    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(625, 150);

    private boolean dialogResult;

    private final EcaServiceOptionsTableModel ecaServiceOptionsTableModel = new EcaServiceOptionsTableModel();

    public EcaServiceOptionsDialog(Window parent) {
        super(parent, TITLE_TEXT);
        this.setModal(true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        JScrollPane scrollPanel = new JScrollPane(new EcaServicePropertiesTable(ecaServiceOptionsTableModel));
        scrollPanel.setPreferredSize(SCROLL_PANE_PREFERRED_SIZE);

        JButton okButton = ButtonUtils.createOkButton();

        JButton cancelButton = ButtonUtils.createCancelButton();

        okButton.addActionListener(evt -> {
            if (!isValidOptions()) {
                dialogResult = false;
            } else {
                setEcaServiceOptions();
                dialogResult = true;
                setVisible(false);
            }
        });

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });

        this.add(scrollPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(5, 5, 10, 5), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(4, 3, 4, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public boolean isDialogResult() {
        return dialogResult;
    }

    private boolean isValidOptions() {
        for (Entry<String, String> entry : ecaServiceOptionsTableModel.getOptions()) {
            if (StringUtils.isEmpty(entry.getValue())) {
                JOptionPane.showMessageDialog(EcaServiceOptionsDialog.this,
                        String.format(EMPTY_PROPERTY_ERROR_FORMAT, entry.getKey()), null, JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void setEcaServiceOptions() {
        ecaServiceOptionsTableModel.getOptions().forEach(this::setOptions);
    }

    private void setOptions(Entry<String, String> entry) {
        switch (entry.getKey()) {
            case CommonDictionary.ECA_SERVICE_ENABLED:
                CONFIG_SERVICE.getEcaServiceConfig().setEnabled(Boolean.valueOf(entry.getValue()));
                break;
            case CommonDictionary.RABBIT_HOST:
                CONFIG_SERVICE.getEcaServiceConfig().setHost(entry.getValue());
                break;
            case CommonDictionary.RABBIT_PORT:
                CONFIG_SERVICE.getEcaServiceConfig().setPort(Integer.parseInt(entry.getValue()));
                break;
            case CommonDictionary.RABBIT_USERNAME:
                CONFIG_SERVICE.getEcaServiceConfig().setUsername(entry.getValue());
                break;
            case CommonDictionary.RABBIT_PASSWORD:
                CONFIG_SERVICE.getEcaServiceConfig().setPassword(entry.getValue());
                break;
            case CommonDictionary.EVALUATION_REQUEST_QUEUE:
                CONFIG_SERVICE.getEcaServiceConfig().setEvaluationRequestQueue(entry.getValue());
                break;
            case CommonDictionary.EVALUATION_OPTIMIZER_REQUEST_QUEUE:
                CONFIG_SERVICE.getEcaServiceConfig().setEvaluationOptimizerRequestQueue(entry.getValue());
                break;
            case CommonDictionary.EXPERIMENT_REQUEST_QUEUE:
                CONFIG_SERVICE.getEcaServiceConfig().setExperimentRequestQueue(entry.getValue());
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected eca-service option %s!", entry.getKey()));
        }
    }

}
