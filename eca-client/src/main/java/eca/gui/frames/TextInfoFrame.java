package eca.gui.frames;

import eca.gui.logging.LoggerUtils;
import eca.converters.TextSaver;
import eca.gui.ButtonUtils;
import eca.gui.choosers.SaveModelChooser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Frame showing text information.
 * @author Roman Batygin
 */
@Slf4j
public class TextInfoFrame extends JFrame {

    private static final String FILE_MENU_TEXT = "Файл";
    private static final String SAVE_MENU_TEXT = "Сохранить";
    private static final Font DEFAULT_TEXT_AREA_FONT = new Font("Arial", Font.BOLD, 12);
    private JTextArea textInfo = new JTextArea(15, 40);
    ;

    public TextInfoFrame(String title, String str, JFrame parent) {
        this.setLayout(new GridBagLayout());
        this.setTitle(title);
        try {
            this.setIconImage(parent.getIconImage());
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
        textInfo.setWrapStyleWord(true);
        textInfo.setLineWrap(true);
        textInfo.setEditable(false);
        textInfo.setFont(DEFAULT_TEXT_AREA_FONT);
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
        JMenu fileMenu = new JMenu(FILE_MENU_TEXT);
        JMenuItem saveMenu = new JMenuItem(SAVE_MENU_TEXT);
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
                    File file = fileChooser.getSelectedFile(TextInfoFrame.this);
                    if (file != null) {
                        TextSaver.saveToFile(file, textInfo.getText());
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(TextInfoFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.setJMenuBar(menu);

        textInfo.setText(str);
        textInfo.setCaretPosition(0);
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

}
