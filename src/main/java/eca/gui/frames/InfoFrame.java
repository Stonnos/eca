package eca.gui.frames;

import eca.core.converters.TextSaver;
import eca.gui.ButtonUtils;
import eca.gui.choosers.SaveModelChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author Roman Batygin
 */

public class InfoFrame extends JFrame {

    private JTextArea textInfo;

    public InfoFrame(String title, String str, JFrame parent) {
        this.setLayout(new GridBagLayout());
        this.setTitle(title);
        try {
            this.setIconImage(parent.getIconImage());
        } catch (Exception e) {
        }
        textInfo = new JTextArea(15, 40);
        textInfo.setWrapStyleWord(true);
        textInfo.setLineWrap(true);
        textInfo.setEditable(false);
        textInfo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        //----------------------------------------
        JScrollPane scrollPanel = new JScrollPane(textInfo);
        JButton okButton = ButtonUtils.createOkButton();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        //----------------------------------------
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(4, 0, 4, 0), 0, 0));
        //---------------------------------------
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem saveMenu = new JMenuItem("Сохранить");
        fileMenu.add(saveMenu);
        menu.add(fileMenu);
        //--------------------------------------------
        saveMenu.addActionListener(new ActionListener() {

            SaveModelChooser fileChooser;

            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (fileChooser == null) {
                        fileChooser = new SaveModelChooser();
                    }
                    File file = fileChooser.saveFile(InfoFrame.this);
                    if (file != null) {
                        TextSaver.saveToFile(file, textInfo.getText());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(InfoFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.setJMenuBar(menu);
        //----------------------------------------
        textInfo.setText(str);
        textInfo.setCaretPosition(0);
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

}
