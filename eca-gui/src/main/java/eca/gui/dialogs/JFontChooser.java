package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.ButtonUtils;
import eca.gui.GuiUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.font.FontManager;
import lombok.extern.slf4j.Slf4j;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;

import static eca.gui.GuiUtils.removeComponents;

/**
 * Font options dialog.
 *
 * @author Roman Batygin
 */
@Slf4j
public class JFontChooser extends JDialog {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 50;
    private static final String FONT_TYPE_TITLE = "Шрифт:";
    private static final String FONT_STYLE_TITLE = "Начертание:";
    private static final String FONT_SIZE_TITLE = "Размер:";
    private static final String[] STYLES = {"Обычный", "Полужирный",
            "Курсив", "Полужирный курсив"};
    private static final String TITLE_TEXT = "Настройки";
    private static final String SELECT_FONT_TITLE = "Выбор шрифта";
    private static final String FONT_EXAMPLE_TITLE = "Образец";
    private static final String FONT_EXAMPLE = "Аа Яя Aa Zz";
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final Dimension COMBO_BOX_DIM = new Dimension(175, 25);
    private static final String DEFAULT_FONT_NAME = "Arial";
    private static final int EXAMPLE_FIELD_ROWS = 3;
    private static final int EXAMPLE_FIELD_COLUMNS = 5;
    private static final int PLAIN_ID = 0;
    private static final int BOLD_ID = 1;
    private static final int ITALIC_ID = 2;
    private static final int BOLD_AND_ITALIC_ID = 3;

    private JComboBox<String> fontNameBox;
    private JComboBox<String> fontSize;
    private JComboBox<String> fontStyle;
    private JTextArea exampleField;

    private boolean dialogResult;

    /**
     * Creates font chooser dialog.
     *
     * @param parent - parent window
     * @param font   - font
     */
    public JFontChooser(Window parent, Font font) {
        super(parent, TITLE_TEXT);
        this.setModal(true);
        this.setResizable(false);
        this.createGUI(font);
        this.setExample();
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    @Override
    public void dispose() {
        exampleField = null;
        fontStyle.removeAll();
        fontSize.removeAll();
        fontNameBox.removeAll();
        removeComponents(this);
        super.dispose();
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    /**
     * Returns selected font name.
     *
     * @return selected font name
     */
    public String getFontName() {
        return fontNameBox.getSelectedItem().toString();
    }

    /**
     * Returns selected font size.
     *
     * @return selected font size
     */
    public int getFontSize() {
        return Integer.parseInt(fontSize.getSelectedItem().toString());
    }

    /**
     * Returns selected font style.
     *
     * @return selected font style
     */
    public int getFontStyle() {
        int index = fontStyle.getSelectedIndex();
        switch (index) {
            case PLAIN_ID:
                return Font.PLAIN;
            case BOLD_ID:
                return Font.BOLD;
            case ITALIC_ID:
                return Font.ITALIC;
            case BOLD_AND_ITALIC_ID:
                return Font.BOLD | Font.ITALIC;
            default:
                throw new IllegalArgumentException(String.format("Unexpected font index: %d", index));
        }
    }

    /**
     * Returns selected font.
     *
     * @return selected font
     */
    public Font getSelectedFont() {
        return new Font(getFontName(), getFontStyle(), getFontSize());
    }

    private void createGUI(Font font) {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        GuiUtils.setIcon(this, CONFIG_SERVICE.getIconUrl(IconType.MAIN_ICON));
        panel.setBorder(PanelBorderUtils.createTitledBorder(SELECT_FONT_TITLE));

        createFontSizeComponent(font);
        createFontStyleComponent(font);
        createFontTypeComponent(font);
        ItemListener listener = event -> setExample();
        fontNameBox.addItemListener(listener);
        fontSize.addItemListener(listener);
        fontStyle.addItemListener(listener);

        exampleField = new JTextArea(EXAMPLE_FIELD_ROWS, EXAMPLE_FIELD_COLUMNS);
        exampleField.setWrapStyleWord(true);
        exampleField.setLineWrap(true);
        exampleField.setEditable(false);
        JScrollPane bottom = new JScrollPane(exampleField);
        bottom.setBorder(PanelBorderUtils.createTitledBorder(FONT_EXAMPLE_TITLE));

        panel.add(new JLabel(FONT_TYPE_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontNameBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        panel.add(new JLabel(FONT_SIZE_TITLE), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontSize, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        panel.add(new JLabel(FONT_STYLE_TITLE), new GridBagConstraints(0, 2, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontStyle, new GridBagConstraints(1, 2, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        panel.add(bottom, new GridBagConstraints(0, 3, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 0, 10, 0), 0, 0));

        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });

        okButton.addActionListener(e -> {
            dialogResult = true;
            setVisible(false);
        });

        this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));

        this.getRootPane().setDefaultButton(okButton);
    }

    private void setExample() {
        exampleField.setFont(getSelectedFont());
        exampleField.setText(FONT_EXAMPLE);
    }

    private void createFontSizeComponent(Font font) {
        fontSize = new JComboBox<>();
        fontSize.setPreferredSize(COMBO_BOX_DIM);
        for (int i = MIN_FONT_SIZE; i <= MAX_FONT_SIZE; i++) {
            fontSize.addItem(String.valueOf(i));
        }
        fontSize.setSelectedItem(String.valueOf(font.getSize()));
    }

    private void createFontTypeComponent(Font font) {
        String[] availableFontNames = FontManager.getFontManager().getAvailableFontNames();
        Font[] allFonts = FontManager.getFontManager().getAllFonts();
        fontNameBox = new JComboBox<>(availableFontNames);
        fontNameBox.setPreferredSize(COMBO_BOX_DIM);
        fontNameBox.setPrototypeDisplayValue(allFonts[0].getFontName());
        Accessible accessibleChild = fontNameBox.getUI().getAccessibleChild(fontNameBox, 0);
        if (accessibleChild instanceof javax.swing.plaf.basic.ComboPopup) {
            JList popupList = ((javax.swing.plaf.basic.ComboPopup) accessibleChild).getList();
            popupList.setPrototypeCellValue(fontNameBox.getPrototypeDisplayValue());
        }
        fontNameBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
                JLabel label = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                if (i >= 0) {
                    label.setFont(allFonts[i]);
                    label.setText(allFonts[i].getFontName());
                }
                return label;
            }
        });
        fontNameBox.setSelectedItem(font.getName());
    }

    private void createFontStyleComponent(Font font) {
        fontStyle = new JComboBox<>(STYLES);
        fontStyle.setPreferredSize(COMBO_BOX_DIM);
        fontStyle.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
                JLabel label = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                if (i >= 0) {
                    switch (i) {
                        case PLAIN_ID:
                            label.setFont(new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE));
                            break;
                        case BOLD_ID:
                            label.setFont(new Font(DEFAULT_FONT_NAME, Font.BOLD, DEFAULT_FONT_SIZE));
                            break;
                        case ITALIC_ID:
                            label.setFont(new Font(DEFAULT_FONT_NAME, Font.ITALIC, DEFAULT_FONT_SIZE));
                            break;
                        case BOLD_AND_ITALIC_ID:
                            label.setFont(new Font(DEFAULT_FONT_NAME, Font.BOLD | Font.ITALIC, DEFAULT_FONT_SIZE));
                            break;
                        default:
                            throw new IllegalStateException("Unexpected font style!");
                    }
                    label.setText(STYLES[i]);
                }
                return label;
            }
        });
        setFontStyle(font);
    }

    private void setFontStyle(Font font) {
        switch (font.getStyle()) {
            case Font.PLAIN:
                fontStyle.setSelectedIndex(PLAIN_ID);
                break;
            case Font.BOLD:
                fontStyle.setSelectedIndex(BOLD_ID);
                break;
            case Font.ITALIC:
                fontStyle.setSelectedIndex(ITALIC_ID);
                break;
            case Font.ITALIC | Font.BOLD:
                fontStyle.setSelectedIndex(BOLD_AND_ITALIC_ID);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unexpected font style: %d", font.getStyle()));
        }
    }

}
