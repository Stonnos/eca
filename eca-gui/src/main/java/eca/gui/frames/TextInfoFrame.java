package eca.gui.frames;

import eca.gui.ButtonUtils;
import eca.gui.choosers.SaveModelChooser;
import eca.gui.logging.LoggerUtils;
import eca.util.TextSaver;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Frame showing text information.
 *
 * @author Roman Batygin
 */
@Slf4j
public class TextInfoFrame extends JFrame {

    private static final String FILE_MENU_TEXT = "Файл";
    private static final String SAVE_MENU_TEXT = "Сохранить";
    private static final Dimension OPTIONS_PANE_PREFERRED_SIZE = new Dimension(475, 200);
    private static final String CONTENT_TYPE = "text/html";
    private JTextPane inputOptionsPane = new JTextPane();

    public TextInfoFrame(String title, String text, JFrame parent) {
        this.setLayout(new GridBagLayout());
        this.setTitle(title);
        try {
            this.setIconImage(parent.getIconImage());
        } catch (Exception e) {
            LoggerUtils.error(log, e);
        }
        inputOptionsPane.setContentType(CONTENT_TYPE);
        inputOptionsPane.setEditable(false);
        inputOptionsPane.setPreferredSize(OPTIONS_PANE_PREFERRED_SIZE);
        JScrollPane scrollPanel = new JScrollPane(inputOptionsPane);
        JButton okButton = ButtonUtils.createOkButton();

        okButton.addActionListener(e -> setVisible(false));
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
                        TextSaver.saveToFile(file, inputOptionsPane.getText());
                    }
                } catch (Exception e) {
                    LoggerUtils.error(log, e);
                    JOptionPane.showMessageDialog(TextInfoFrame.this, e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        this.setJMenuBar(menu);
        inputOptionsPane.setText(text);
        inputOptionsPane.setCaretPosition(0);
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

}
