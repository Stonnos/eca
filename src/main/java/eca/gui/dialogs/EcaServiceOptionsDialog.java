package eca.gui.dialogs;

import eca.EcaServiceProperties;
import eca.beans.Entry;
import eca.gui.ButtonUtils;
import eca.gui.tables.EcaServicePropertiesTable;
import eca.gui.tables.models.EcaServiceOptionsTableModel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

/**
 * @author Roman Batygin
 */
public class EcaServiceOptionsDialog extends JDialog {

    private static final EcaServiceProperties PROPERTIES = EcaServiceProperties.getInstance();

    private static final String TITLE = "Настройки сервиса ECA";

    private final EcaServiceOptionsTableModel model = new EcaServiceOptionsTableModel();

    public EcaServiceOptionsDialog(Window parent) {
        super(parent, TITLE);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        JScrollPane scrollPanel = new JScrollPane(new EcaServicePropertiesTable(model));
        scrollPanel.setPreferredSize(new Dimension(500,200));

        JButton okButton = ButtonUtils.createOkButton();

        JButton cancelButton = ButtonUtils.createCancelButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    for (Iterator<Entry> iterator = model.getOptions(); iterator.hasNext(); ) {
                        Entry entry = iterator.next();
                        if (StringUtils.isBlank(entry.getValue())) {
                            throw new Exception(String.format("Укажите значение свойства '%s'",
                                    entry.getKey()));
                        }

                        if (entry.getKey().equals(EcaServiceProperties.ECA_SERVICE_ENABLED)) {
                            if (!entry.getValue().equalsIgnoreCase("false") &&
                                    !entry.getValue().equalsIgnoreCase("true")) {
                                throw new Exception(String.format("Недопустимое значение свойства '%s'",
                                        entry.getKey()));
                            }
                        }

                        PROPERTIES.put(entry.getKey(), entry.getValue());
                    }
                    PROPERTIES.save();
                    setVisible(false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EcaServiceOptionsDialog.this,
                            e.getMessage(), "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });

        this.add(scrollPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
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

}
