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
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Roman Batygin
 */
public class EcaServiceOptionsDialog extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String TITLE_TEXT = "Настройки сервиса ECA";
    private static final String EMPTY_PROPERTY_ERROR_FORMAT = "Укажите значение свойства '%s'";
    private static final String INVALID_PROPERTY_ERROR_FORMAT = "Недопустимое значение свойства '%s'";
    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(500, 150);

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
            try {
                saveEcaServiceOptions();
                setVisible(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(EcaServiceOptionsDialog.this,
                        e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> setVisible(false));

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

    private void saveEcaServiceOptions() throws IOException {
        for (Iterator<Entry> iterator = ecaServiceOptionsTableModel.getOptions(); iterator.hasNext(); ) {
            Entry entry = iterator.next();
            if (StringUtils.isEmpty(entry.getValue())) {
                throw new IllegalArgumentException(
                        String.format(EMPTY_PROPERTY_ERROR_FORMAT, entry.getKey()));
            }

            if (entry.getKey().equals(CommonDictionary.ECA_SERVICE_ENABLED) &&
                    !entry.getValue().equalsIgnoreCase(Boolean.FALSE.toString().toLowerCase()) &&
                    !entry.getValue().equalsIgnoreCase(Boolean.TRUE.toString().toLowerCase())) {
                throw new IllegalArgumentException(
                        String.format(INVALID_PROPERTY_ERROR_FORMAT, entry.getKey()));
            }
            setOptions(entry);
        }
        CONFIG_SERVICE.saveEcaServiceConfig();
    }

    private void setOptions(Entry entry) {
        switch (entry.getKey()) {
            case CommonDictionary.ECA_SERVICE_ENABLED:
                CONFIG_SERVICE.getEcaServiceConfig().setEnabled(Boolean.valueOf(entry.getValue()));
                break;
            case CommonDictionary.ECA_SERVICE_URL:
                CONFIG_SERVICE.getEcaServiceConfig().setEvaluationUrl(entry.getValue());
                break;
            case CommonDictionary.ECA_SERVICE_EXPERIMENT_URL:
                CONFIG_SERVICE.getEcaServiceConfig().setExperimentUrl(entry.getValue());
                break;
            case CommonDictionary.ECA_SERVICE_OPTIMAL_CLASSIFIER_URL:
                CONFIG_SERVICE.getEcaServiceConfig().setOptimalClassifierUrl(entry.getValue());
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected eca-service option %s!", entry.getKey()));
        }
    }

}
