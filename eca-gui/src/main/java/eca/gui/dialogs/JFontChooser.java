/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.config.ConfigurationService;
import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.logging.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.net.URL;

/**
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

    private JComboBox<String> fontTypeBox;
    private JComboBox<String> fontSize;
    private JComboBox<String> fontStyle;
    private JTextArea exampleField;

    private static String[] FONTS;

    private boolean dialogResult;

    static {
        try {
            FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
        }
    }

    public JFontChooser(Window parent, Font font) {
        super(parent, TITLE_TEXT);
        this.setModal(true);
        this.setResizable(false);
        this.createGUI(font);
        this.pack();
        this.setLocationRelativeTo(parent);
    }

    public boolean dialogResult() {
        return dialogResult;
    }

    public String getFontType() {
        return fontTypeBox.getSelectedItem().toString();
    }

    public int getFontSize() {
        return Integer.parseInt(fontSize.getSelectedItem().toString());
    }

    public int getFontTracing() {
        int index = fontStyle.getSelectedIndex();
        int style;
        switch (index) {
            case PLAIN_ID:
                style = Font.PLAIN;
                break;
            case BOLD_ID:
                style = Font.BOLD;
                break;
            case ITALIC_ID:
                style = Font.ITALIC;
                break;
            case BOLD_AND_ITALIC_ID:
                style = Font.BOLD | Font.ITALIC;
                break;

            default:
                throw new IllegalArgumentException(String.format("Unexpected font style: %d", index));
        }
        return style;
    }

    public Font getSelectedFont() {
        return new Font(getFontType(), getFontTracing(), getFontSize());
    }

    private void createGUI(Font font) {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        this.setIcon();
        panel.setBorder(PanelBorderUtils.createTitledBorder(SELECT_FONT_TITLE));
        //---------------------------------
        fontTypeBox = new JComboBox<>(FONTS);
        fontSize = new JComboBox<>();
        fontStyle = new JComboBox<>(STYLES);
        fontTypeBox.setPreferredSize(COMBO_BOX_DIM);
        fontSize.setPreferredSize(COMBO_BOX_DIM);
        fontStyle.setPreferredSize(COMBO_BOX_DIM);
        fontTypeBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
                JLabel label = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                if (i >= 0) {
                    label.setFont(new Font(FONTS[i], Font.PLAIN, DEFAULT_FONT_SIZE));
                    label.setText(FONTS[i]);
                }
                return label;
            }
        });

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
                    }
                    label.setText(STYLES[i]);
                }
                return label;
            }
        });

        //---------------------------------
        for (int i = MIN_FONT_SIZE; i <= MAX_FONT_SIZE; i++) {
            fontSize.addItem(String.valueOf(i));
        }
        fontSize.setSelectedItem(String.valueOf(font.getSize()));
        fontTypeBox.setSelectedItem(font.getName());
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
        }
        //----------------------------------
        ItemListener listener = event -> setExample();
        fontTypeBox.addItemListener(listener);
        fontSize.addItemListener(listener);
        fontStyle.addItemListener(listener);
        //----------------------------------
        exampleField = new JTextArea(EXAMPLE_FIELD_ROWS, EXAMPLE_FIELD_COLUMNS);
        exampleField.setWrapStyleWord(true);
        exampleField.setLineWrap(true);
        exampleField.setEditable(false);
        JScrollPane bottom = new JScrollPane(exampleField);
        bottom.setBorder(PanelBorderUtils.createTitledBorder(FONT_EXAMPLE_TITLE));
        setExample();
        //---------------------------------
        panel.add(new JLabel(FONT_TYPE_TITLE),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontTypeBox, new GridBagConstraints(1, 0, 1, 1, 1, 1,
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
        //---------------------------------
        JButton okButton = ButtonUtils.createOkButton();
        JButton cancelButton = ButtonUtils.createCancelButton();

        cancelButton.addActionListener(e -> {
            dialogResult = false;
            setVisible(false);
        });
        //-----------------------------------------------
        okButton.addActionListener(e -> {
            dialogResult = true;
            setVisible(false);
        });
        //--------------------------------------------------------------------
        this.add(panel, new GridBagConstraints(0, 0, 2, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 10, 0), 0, 0));
        this.add(okButton, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 8, 3), 0, 0));
        this.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 8, 0), 0, 0));
        //-----------------------------------------------
        this.getRootPane().setDefaultButton(okButton);
    }

    private void setIcon() {
        try {
            URL iconUrl = getClass().getClassLoader().getResource(CONFIG_SERVICE.getApplicationConfig().getIconUrl());
            if (iconUrl != null) {
                this.setIconImage(ImageIO.read(iconUrl));
            }
        } catch (Exception ex) {
            LoggerUtils.error(log, ex);
        }
    }

    private void setExample() {
        exampleField.setFont(getSelectedFont());
        exampleField.setText(FONT_EXAMPLE);
    }

}
