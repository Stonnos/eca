package eca.gui.frames;

import eca.gui.ButtonUtils;
import eca.gui.dialogs.JFontChooser;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ConsoleFrame extends JFrame {

    private static final String CONSOLE_TITLE = "Консоль";
    public static final String OPTIONS_MENU_TEXT = "Настройки";
    public static final String SELECTED_FONT_MENU_TEXT = "Выбор шрифта";
    public static final String BACKGROUND_COLOR_MENU_TEXT = "Выбор цвета фона";
    public static final String FONT_COLOR_MENU_TEXT = "Выбор цвета шрифта";

    private JTextArea textArea;


    public ConsoleFrame(JFrame parent, JTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new GridBagLayout());
        this.setTitle(CONSOLE_TITLE);
        this.createMenu();
        try {
            this.setIconImage(parent.getIconImage());
        } catch (Exception e) {
            log.warn("There was an error:", e);
        }
        //----------------------------------------
        JScrollPane scrollPanel = new JScrollPane(this.textArea);
        JButton closeButton = ButtonUtils.createCloseButton();

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });
        //----------------------------------------
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 0, 10, 0), 0, 0));

        this.getRootPane().setDefaultButton(closeButton);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu(OPTIONS_MENU_TEXT);
        JMenuItem fontMenu = new JMenuItem(SELECTED_FONT_MENU_TEXT);
        JMenuItem backgroundColorMenu = new JMenuItem(BACKGROUND_COLOR_MENU_TEXT);
        JMenuItem fontColorMenu = new JMenuItem(FONT_COLOR_MENU_TEXT);
        fileMenu.add(fontMenu);
        fileMenu.add(fontColorMenu);
        fileMenu.add(backgroundColorMenu);
        menu.add(fileMenu);
        //--------------------------------------------
        fontMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                JFontChooser chooser = new JFontChooser(ConsoleFrame.this,
                        textArea.getFont());
                chooser.setVisible(true);
                if (chooser.dialogResult()) {
                    Font selectedFont = chooser.getSelectedFont();
                    textArea.setFont(selectedFont);
                }
                chooser.dispose();
            }
        });
        fontColorMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, FONT_COLOR_MENU_TEXT,
                        textArea.getForeground());
                if (selectedColor != null) {
                    textArea.setForeground(selectedColor);
                }
            }
        });
        backgroundColorMenu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, BACKGROUND_COLOR_MENU_TEXT,
                        textArea.getForeground());
                if (selectedColor != null) {
                    textArea.setBackground(selectedColor);
                }
            }
        });
        this.setJMenuBar(menu);
    }
}
