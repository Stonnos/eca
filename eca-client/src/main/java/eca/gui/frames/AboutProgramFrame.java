/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.config.ApplicationProperties;
import eca.gui.AboutProgramService;
import eca.gui.ButtonUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
public class AboutProgramFrame extends JFrame {

    private static final ApplicationProperties APPLICATION_PROPERTIES = ApplicationProperties.getInstance();
    private static final String ABOUT_PROGRAM_TITLE = "О программе";

    public AboutProgramFrame(JFrame parent) {
        this.setIconImage(parent.getIconImage());
        this.makeGUI();
        this.setLocationRelativeTo(parent);
    }

    private void makeGUI() {
        this.setResizable(false);
        this.setTitle(ABOUT_PROGRAM_TITLE);
        this.setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();
        JLabel info = new JLabel(AboutProgramService.getAboutProgramHtmlString());
        ImageIcon icon =
                new ImageIcon(getClass().getClassLoader().getResource(APPLICATION_PROPERTIES.getLogotypeUrl()));
        info.setIcon(icon);
        infoPanel.add(info);
        JButton okButton = ButtonUtils.createCloseButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
        this.pack();
    }

}
