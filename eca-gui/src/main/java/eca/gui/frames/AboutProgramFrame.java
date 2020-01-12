/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.config.ConfigurationService;
import eca.gui.ButtonUtils;
import eca.gui.service.AboutProgramService;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class AboutProgramFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();
    private static final String ABOUT_PROGRAM_TITLE = "О программе";

    public AboutProgramFrame(JFrame parent) {
        this.setIconImage(parent.getIconImage());
        this.createGUI();
        this.setLocationRelativeTo(parent);
    }

    private void createGUI() {
        this.setResizable(false);
        this.setTitle(ABOUT_PROGRAM_TITLE);
        this.setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();
        JLabel info = new JLabel(AboutProgramService.getAboutProgramHtmlString());
        ImageIcon icon = new ImageIcon(
                getClass().getClassLoader().getResource(CONFIG_SERVICE.getApplicationConfig().getLogotypeUrl()));
        info.setIcon(icon);
        infoPanel.add(info);
        JButton closeButton = ButtonUtils.createCloseButton();

        closeButton.addActionListener(e -> setVisible(false));
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        this.pack();
    }

}
