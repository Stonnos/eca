package eca.gui.frames;

import eca.buffer.StringCopier;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.core.TextSearcher;
import eca.gui.ButtonUtils;
import eca.gui.dialogs.JFontChooser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

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
    private static final String SEARCH_MENU_TEXT = "Поиск";
    private static final Dimension SEARCH_TEXT_PREFERRED_SIZE = new Dimension(225, 25);
    private static final Dimension SEARCH_BUTTON_PREFERRED_SIZE = new Dimension(140, 25);

    private JTextArea textArea;

    public ConsoleFrame(JFrame parent, JTextArea textArea) {
        this.textArea = textArea;
        this.setLayout(new GridBagLayout());
        this.setTitle(CONSOLE_TITLE);
        this.createPopMenu();
        this.setIconImage(parent.getIconImage());
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
        JMenuItem copyMenuItem = createCopyMenuItem();
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                copyMenuItem.setEnabled(!StringUtils.isEmpty(textArea.getSelectedText()));
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
        popupMenu.add(createSearchMenuItem());
        popupMenu.add(createFontMenuItem());
        popupMenu.add(createFontColorMenuItem());
        popupMenu.add(createBackgroundColorMenuItem());
        popupMenu.add(copyMenuItem);
        textArea.setComponentPopupMenu(popupMenu);
    }

    private JMenuItem createCopyMenuItem() {
        JMenuItem copyMenuItem = new JMenuItem(DATA_COPY_MENU_TEXT);
        copyMenuItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COPY_ICON)));
        copyMenuItem.addActionListener(new ActionListener() {

            StringCopier stringCopier = new StringCopier();

            @Override
            public void actionPerformed(ActionEvent evt) {
                stringCopier.setCopyString(textArea.getSelectedText());
                stringCopier.copy();
            }
        });
        return copyMenuItem;
    }

    private JMenuItem createBackgroundColorMenuItem() {
        JMenuItem backgroundColorMenuItem = new JMenuItem(BACKGROUND_COLOR_MENU_TEXT);
        backgroundColorMenuItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COLOR_ICON)));
        backgroundColorMenuItem.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, BACKGROUND_COLOR_MENU_TEXT,
                    textArea.getForeground());
            if (selectedColor != null) {
                textArea.setBackground(selectedColor);
            }
        });
        return backgroundColorMenuItem;
    }

    private JMenuItem createFontMenuItem() {
        JMenuItem fontMenuItem = new JMenuItem(SELECTED_FONT_MENU_TEXT);
        fontMenuItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.FONT_ICON)));
        fontMenuItem.addActionListener(e -> {
            JFontChooser chooser = new JFontChooser(ConsoleFrame.this, textArea.getFont());
            chooser.setVisible(true);
            if (chooser.dialogResult()) {
                Font selectedFont = chooser.getSelectedFont();
                textArea.setFont(selectedFont);
            }
            chooser.dispose();
        });
        return fontMenuItem;
    }

    private JMenuItem createFontColorMenuItem() {
        JMenuItem fontColorMenu = new JMenuItem(FONT_COLOR_MENU_TEXT);
        fontColorMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COLOR_ICON)));
        fontColorMenu.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(ConsoleFrame.this, FONT_COLOR_MENU_TEXT,
                    textArea.getForeground());
            if (selectedColor != null) {
                textArea.setForeground(selectedColor);
            }
        });
        return fontColorMenu;
    }

    private JMenuItem createSearchMenuItem() {
        JMenuItem searchMenuItem = new JMenuItem(SEARCH_MENU_TEXT);
        searchMenuItem.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.SEARCH_ICON)));
        searchMenuItem.addActionListener(actionEvent -> {
            TextSearchDialog textSearchDialog = new TextSearchDialog();
            textSearchDialog.setVisible(true);
        });
        return searchMenuItem;
    }

    private class TextSearchDialog extends JDialog {

        static final String TITLE = "Поиск";
        static final String SEARCH_BUTTON_TEXT = "Найти далее";
        static final int DIALOG_MARGIN_LEFT = 25;
        static final int DIALOG_MARGIN_TOP = 40;
        static final String MATCH_NOT_FOUND_TEXT_FORMAT = "Не удалось найти '%s'";

        String searchTerm;
        TextSearcher textSearcher;

        TextSearchDialog() {
            super(ConsoleFrame.this, TITLE);
            this.setModal(false);
            this.setResizable(false);
            this.setLayout(new GridBagLayout());
            this.createGUI();
            this.pack();
            this.internalSetLocation();
        }

        void internalSetLocation() {
            int x = ConsoleFrame.this.getX() + ConsoleFrame.this.getWidth() - getWidth() - DIALOG_MARGIN_LEFT;
            int y = ConsoleFrame.this.getY() + DIALOG_MARGIN_TOP;
            this.setLocation(x, y);
        }

        void createGUI() {
            JPanel optionPanel = new JPanel(new GridBagLayout());
            JButton searchButton = ButtonUtils.createButton(SEARCH_BUTTON_TEXT);
            searchButton.setPreferredSize(SEARCH_BUTTON_PREFERRED_SIZE);
            searchButton.setMinimumSize(SEARCH_BUTTON_PREFERRED_SIZE);
            searchButton.setEnabled(false);

            JTextField searchTextField = createSearchTextField(searchButton);

            JButton cancelButton = ButtonUtils.createCancelButton();

            cancelButton.addActionListener(e -> {
                clearSelection();
                setVisible(false);
            });
            searchButton.addActionListener(searchListener(searchTextField));
            optionPanel.add(searchTextField, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 5, 10, 5), 0, 0));
            this.add(optionPanel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));
            this.add(searchButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                    GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 8, 3), 0, 0));
            this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 5), 0, 0));
            this.getRootPane().setDefaultButton(searchButton);
        }

        private void clearSelection() {
            textArea.setSelectionStart(0);
            textArea.setSelectionEnd(0);
        }

        JTextField createSearchTextField(JButton searchButton) {
            JTextField textField = new JTextField();
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    searchButton.setEnabled(StringUtils.isNotBlank(textField.getText()));
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    searchButton.setEnabled(StringUtils.isNotBlank(textField.getText()));
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    searchButton.setEnabled(StringUtils.isNotBlank(textField.getText()));
                }
            });
            textField.setPreferredSize(SEARCH_TEXT_PREFERRED_SIZE);
            return textField;
        }

        ActionListener searchListener(JTextField textField) {
            return event -> {
                if (!Objects.equals(searchTerm, textField.getText())) {
                    searchTerm = textField.getText();
                    textSearcher = new TextSearcher(textArea.getText(), searchTerm);
                }
                if (!textSearcher.find()) {
                    clearSelection();
                    JOptionPane.showMessageDialog(ConsoleFrame.this,
                            String.format(MATCH_NOT_FOUND_TEXT_FORMAT, searchTerm), null,
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    textArea.setSelectionStart(textSearcher.getCurrentMatchStartPosition());
                    textArea.setSelectionEnd(textSearcher.getCurrentMatchEndPosition());
                }
            };
        }

    }
}
