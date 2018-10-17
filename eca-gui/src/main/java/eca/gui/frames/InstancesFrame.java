package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.tables.ResultInstancesTable;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
@Slf4j
public class InstancesFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String DATA_FORMAT = "Данные: %s";

    private Instances data;

    public InstancesFrame(Instances data, Window parent) {
        this.data = data;
        this.setTitle(String.format(DATA_FORMAT, data.relationName()));
        this.setLayout(new GridBagLayout());
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON), log);
        JScrollPane scrollPanel = new JScrollPane(new ResultInstancesTable(data));
        JButton closeButton = ButtonUtils.createCloseButton();
        closeButton.addActionListener(e -> setVisible(false));
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        this.getRootPane().setDefaultButton(closeButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public Instances getData() {
        return data;
    }
}
