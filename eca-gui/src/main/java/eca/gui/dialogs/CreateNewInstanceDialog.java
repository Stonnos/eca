package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.tables.CreateInstanceTable;
import eca.util.Entry;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author Roman Batygin
 */
public class CreateNewInstanceDialog extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();


    private static final String TITLE_TEXT = "Создание нового объекта";
    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(700, 300);

    private CreateInstanceTable createInstanceTable;
    private boolean dialogResult;

    public CreateNewInstanceDialog(Window parent, List<Entry<String, Integer>> attributes) {
        super(parent, TITLE_TEXT);
        this.setModal(true);
        this.setLayout(new GridBagLayout());
        this.setResizable(false);
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON));
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        okButton.addActionListener(evt -> {
            try {
                createInstanceTable.validateValues();
                dialogResult = true;
                setVisible(false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(CreateNewInstanceDialog.this,
                        e.getMessage(), null, JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });

        this.add(createInstanceTable(attributes), new GridBagConstraints(0, 0, 2, 1, 1, 1,
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

    public List<Object> getValues() {
        return createInstanceTable.getValues();
    }

    private JScrollPane createInstanceTable(List<Entry<String, Integer>> attributes) {
        createInstanceTable = new CreateInstanceTable(attributes);
        JScrollPane scrollPanel = new JScrollPane(createInstanceTable);
        scrollPanel.setPreferredSize(SCROLL_PANE_PREFERRED_SIZE);
        return scrollPanel;
    }

}
