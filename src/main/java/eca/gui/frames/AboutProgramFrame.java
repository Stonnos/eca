/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.frames;

import eca.ApplicationProperties;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 *
 * @author Рома
 */
public class AboutProgramFrame extends JFrame {


    public AboutProgramFrame(JFrame parent) {
        this.setIconImage(parent.getIconImage());
        this.makeGUI();
        this.setLocationRelativeTo(parent);
    }
    
    private void makeGUI() {
        this.setResizable(false);
        this.setTitle("О программе");
        this.setLayout(new GridBagLayout());

        JPanel infoPanel = new JPanel();
        JLabel info = new JLabel(makeInfo());
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        ImageIcon icon = new ImageIcon(getClass()
                .getClassLoader().getResource(applicationProperties.getLogotypeUrl()));
        info.setIcon(icon);
        infoPanel.add(info);
        JButton okButton = new JButton("Закрыть");
        //-----------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
         }
        );
        add(infoPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1, 
            GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	    add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        //-----------------------------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
    }
    
    private String makeInfo() {
        ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
        StringBuilder info = new StringBuilder("<html><body style = 'font-size: 15'>");
        info.append("<span style = 'font-size: 36'>" + applicationProperties.getTitle() + "</span><br>");
        info.append("<i><span style = 'font-size: 28'>" + applicationProperties.getTitleDescription() + "</span></i><br>");
        info.append("<br>&copy Copyright " + applicationProperties.getTitle() + ", Inc. 2015 - 2017<br>");
        info.append("Все права защищены.<br>");
        info.append("Автор: <i>"+ applicationProperties.getAuthor() + "</i><br>");
        info.append("Email: <i>" + applicationProperties.getAuthorEmail() + "</i><br>");
        info.append("Версия: <i>" + applicationProperties.getVersion()+ "</i><br>");
        info.append("Последняя дата обновления: <i>" + applicationProperties.getReleaseDateToString() + "</i>");
        info.append("</body></html>");
        return info.toString();
    }
    
}
