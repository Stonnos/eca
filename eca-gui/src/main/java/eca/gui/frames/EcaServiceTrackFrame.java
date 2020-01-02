package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.tables.EcaServiceTrackTable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
@Slf4j
public class EcaServiceTrackFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String FRAME_TITLE = "Запросы в Eca - сервис";

    private static final Dimension SCROLL_PANE_PREFERRED_SIZE = new Dimension(600, 150);

    @Getter
    private EcaServiceTrackTable ecaServiceTrackTable = new EcaServiceTrackTable();

    public EcaServiceTrackFrame(Window parent) {
        this.setTitle(FRAME_TITLE);
        this.setLayout(new GridBagLayout());
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON));
        JScrollPane scrollPanel = new JScrollPane(ecaServiceTrackTable);
        scrollPanel.setPreferredSize(SCROLL_PANE_PREFERRED_SIZE);
        JButton closeButton = ButtonUtils.createCloseButton();
        closeButton.addActionListener(e -> setVisible(false));
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        this.pack();
        this.setLocationRelativeTo(parent);
    }
}
