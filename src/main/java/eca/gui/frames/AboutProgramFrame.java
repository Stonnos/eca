/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.ApplicationProperties;

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
    private static final String CLOSE_BUTTON = "Закрыть";

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
        JLabel info = new JLabel(makeInfo());
        ImageIcon icon =
                new ImageIcon(getClass().getClassLoader().getResource(APPLICATION_PROPERTIES.getLogotypeUrl()));
        info.setIcon(icon);
        infoPanel.add(info);
        JButton okButton = new JButton(CLOSE_BUTTON);

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

    private String makeInfo() {
        StringBuilder info = new StringBuilder("<html><body style = 'font-size: 15'>");
        info.append("<span style = 'font-size: 36'>").append(APPLICATION_PROPERTIES.getTitle()).append("</span><br>");
        info.append("<i><span style = 'font-size: 28'>").append(APPLICATION_PROPERTIES.getTitleDescription())
                .append("</span></i><br>");
        info.append("<br>&copy Copyright ").append(APPLICATION_PROPERTIES.getTitle()).append(", Inc. 2015 - 2017<br>");
        info.append("Все права защищены.<br>");
        info.append("Автор: <i>").append(APPLICATION_PROPERTIES.getAuthor()).append("</i><br>");
        info.append("Email: <i>").append(APPLICATION_PROPERTIES.getAuthorEmail()).append("</i><br>");
        info.append("Версия: <i>").append(APPLICATION_PROPERTIES.getVersion()).append("</i><br>");
        info.append("Последняя дата обновления: <i>").append(APPLICATION_PROPERTIES.getReleaseDateToString())
                .append("</i>");
        info.append("</body></html>");
        return info.toString();
    }

}
