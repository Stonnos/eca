package eca.gui.frames;

import eca.buffer.StringCopier;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.dialogs.JFontChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ConsoleFrame extends JFrame {

    private static final ConfigurationService CONFIG_SERVICE = ConfigurationService.getApplicationConfigService();

    private static final String DATA_COPY_MENU_TEXT = "Копировать";
    private static final String CONSOLE_TITLE = "Консоль";
    private static final String SELECTED_FONT_MENU_TEXT = "Выбор шрифта";
    private static final String BACKGROUND_COLOR_MENU_TEXT = "Выбор цвета фона";
    private static final String FONT_COLOR_MENU_TEXT = "Выбор цвета шрифта";

    private JTextArea textArea;


    public ConsoleFrame(JFrame parent, JTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new GridBagLayout());
        this.setTitle(CONSOLE_TITLE);
        this.createPopMenu();
        try {
            this.setIconImage(parent.getIconImage());
        } catch (Exception e) {
            log.warn("There was an error:", e);
        }
        JScrollPane scrollPanel = new JScrollPane(this.textArea);
        JButton closeButton = ButtonUtils.createCloseButton();

        closeButton.addActionListener(e -> setVisible(false));
        this.add(scrollPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(closeButton, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 0, 10, 0), 0, 0));
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    private void createPopMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyMenu = new JMenuItem(DATA_COPY_MENU_TEXT);
        copyMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COPY_ICON)));
        JMenuItem fontMenu = new JMenuItem(SELECTED_FONT_MENU_TEXT);
        JMenuItem backgroundColorMenu = new JMenuItem(BACKGROUND_COLOR_MENU_TEXT);
        JMenuItem fontColorMenu = new JMenuItem(FONT_COLOR_MENU_TEXT);
        fontMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.FONT_ICON)));
        fontMenu.addActionListener(e -> {
            JFontChooser chooser = new JFontChooser(ConsoleFrame.this, textArea.getFont());
            chooser.setVisible(true);
            if (chooser.dialogResult()) {
                Font selectedFont = chooser.getSelectedFont();
                textArea.setFont(selectedFont);
            }
            chooser.dispose();
        });

        fontColorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COLOR_ICON)));
        fontColorMenu.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, FONT_COLOR_MENU_TEXT,
                    textArea.getForeground());
            if (selectedColor != null) {
                textArea.setForeground(selectedColor);
            }
        });

        backgroundColorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COLOR_ICON)));
        backgroundColorMenu.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, BACKGROUND_COLOR_MENU_TEXT,
                    textArea.getForeground());
            if (selectedColor != null) {
                textArea.setBackground(selectedColor);
            }
        });
        copyMenu.addActionListener(new ActionListener() {

            StringCopier stringCopier = new StringCopier();

            @Override
            public void actionPerformed(ActionEvent evt) {
                stringCopier.setCopyString(textArea.getSelectedText());
                stringCopier.copy();
            }
        });
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
               copyMenu.setEnabled(!StringUtils.isEmpty(textArea.getSelectedText()));
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Not implemented
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Not implemented
            }
        });
        popupMenu.add(fontMenu);
        popupMenu.add(fontColorMenu);
        popupMenu.add(backgroundColorMenu);
        popupMenu.add(copyMenu);
        textArea.setComponentPopupMenu(popupMenu);
    }
}
