/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;
import eca.gui.logging.LoggerUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Roman Batygin
 */
@Slf4j
public class JFontChooser extends JDialog {

    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 50;
    private static final String FONT_TYPE_TITLE = "Шрифт:";
    private static final String FONT_STYLE_TITLE = "Начертание:";
    private static final String FONT_SIZE_TITLE = "Размер:";
    private static final String[] STYLES = {"Обычный", "Полужирный",
            "Курсив", "Полужирный курсив"};
    private static final String TITLE = "Настройки";
    private static final String SELECT_FONT_TITLE = "Выбор шрифта";
    private static final String FONT_EXAMPLE_TITLE = "Образец";
    private static final String FONT_EXAMPLE = "Аа Яя Aa Zz";
    private static final int DEFAULT_FONT_SIZE = 12;
    private static final Dimension COMBO_BOX_DIM = new Dimension(175, 25);

    private JComboBox<String> fontTypeBox;
    private JComboBox<String> fontSize;
    private JComboBox<String> fontStyle;
    private JTextArea exampleField;

    private static String[] FONTS;

    private boolean dialogResult;

    static {
        try {
            FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        } catch (Throwable ex) {
            LoggerUtils.error(log, ex);
        }
    }

    public JFontChooser(Window parent, Font font) {
        super(parent, TITLE);
        this.setModal(true);
        this.setResizable(false);
        this.makeGUI(font);
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
        int style = 0;
        switch (index) {
            case 0:
                style = Font.PLAIN;
                break;
            case 1:
                style = Font.BOLD;
                break;
            case 2:
                style = Font.ITALIC;
                break;
            case 3:
                style = Font.BOLD | Font.ITALIC;
                break;
        }
        return style;
    }

    public Font getSelectedFont() {
        return new Font(getFontType(), getFontTracing(), getFontSize());
    }

    private void makeGUI(Font font) {
        this.setLayout(new GridBagLayout());
        JPanel panel = new JPanel(new GridBagLayout());
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
                        case 0:
                            label.setFont(new Font("Arial", Font.PLAIN, DEFAULT_FONT_SIZE));
                            break;
                        case 1:
                            label.setFont(new Font("Arial", Font.BOLD, DEFAULT_FONT_SIZE));
                            break;
                        case 2:
                            label.setFont(new Font("Arial", Font.ITALIC, DEFAULT_FONT_SIZE));
                            break;
                        case 3:
                            label.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, DEFAULT_FONT_SIZE));
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
                fontStyle.setSelectedIndex(0);
                break;
            case Font.BOLD:
                fontStyle.setSelectedIndex(1);
                break;
            case Font.ITALIC:
                fontStyle.setSelectedIndex(2);
                break;
            case Font.ITALIC | Font.BOLD:
                fontStyle.setSelectedIndex(3);
                break;
        }
        //----------------------------------
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                setExample();
            }
        };
        fontTypeBox.addItemListener(listener);
        fontSize.addItemListener(listener);
        fontStyle.addItemListener(listener);
        //----------------------------------
        exampleField = new JTextArea(3, 5);
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

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = false;
                setVisible(false);
            }
        });
        //-----------------------------------------------
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                dialogResult = true;
                setVisible(false);
            }
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

    private void setExample() {
        exampleField.setFont(getSelectedFont());
        exampleField.setText(FONT_EXAMPLE);
    }

}
