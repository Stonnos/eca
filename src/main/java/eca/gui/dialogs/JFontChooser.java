/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.dialogs;

import eca.gui.ButtonUtils;
import eca.gui.PanelBorderUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 * @author Рома
 */
public class JFontChooser extends JDialog {

    private static final int MIN_FONT_SIZE = 8;
    private static final int MAX_FONT_SIZE = 50;
    private static final String fontTypeTitle = "Шрифт:";
    private static final String fontStyleTitle = "Начертание:";
    private static final String fontSizeTitle = "Размер:";
    private static final String[] FONTS = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    private static final String[] STYLES = {"Обычный", "Полужирный",
        "Курсив", "Полужирный курсив"};

    private JComboBox<String> fontType;
    private JComboBox<String> fontSize;
    private JComboBox<String> fontStyle;
    private JTextArea exampleField;

    private boolean dialogResult;

    public JFontChooser(Window parent, Font font) {
        super(parent, "Настройки");
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
        return fontType.getSelectedItem().toString();
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
        panel.setBorder(PanelBorderUtils.createTitledBorder("Выбор шрифта"));
        //---------------------------------
        fontType = new JComboBox<>(FONTS);
        fontSize = new JComboBox<>();
        fontStyle = new JComboBox<>(STYLES);
        Dimension comboBoxDim = new Dimension(175, 25);
        fontType.setPreferredSize(comboBoxDim);
        fontSize.setPreferredSize(comboBoxDim);
        fontStyle.setPreferredSize(comboBoxDim);
        fontType.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> jlist, Object o, int i, boolean bln, boolean bln1) {
                JLabel label = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                if (i >= 0) {
                    label.setFont(new Font(FONTS[i], Font.PLAIN, 12));
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
                            label.setFont(new Font("Arial", Font.PLAIN, 12));
                            break;
                        case 1:
                            label.setFont(new Font("Arial", Font.BOLD, 12));
                            break;
                        case 2:
                            label.setFont(new Font("Arial", Font.ITALIC, 12));
                            break;
                        case 3:
                            label.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
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
        fontType.setSelectedItem(font.getName());
        switch (font.getStyle()) {
            case Font.PLAIN: fontStyle.setSelectedIndex(0); break;
            case Font.BOLD: fontStyle.setSelectedIndex(1); break;
            case Font.ITALIC: fontStyle.setSelectedIndex(2); break;
            case Font.ITALIC | Font.BOLD: fontStyle.setSelectedIndex(3); break;
        }
        //----------------------------------
        ItemListener listener = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                setExample();
            }
        };
        fontType.addItemListener(listener);
        fontSize.addItemListener(listener);
        fontStyle.addItemListener(listener);
        //----------------------------------
        exampleField = new JTextArea(3,5);
        exampleField.setWrapStyleWord(true);
        exampleField.setLineWrap(true);
        exampleField.setEditable(false);
        JScrollPane bottom = new JScrollPane(exampleField);
        bottom.setBorder(PanelBorderUtils.createTitledBorder("Образец"));
        setExample();
        //---------------------------------
        panel.add(new JLabel(fontTypeTitle),
                new GridBagConstraints(0, 0, 1, 1, 1, 1,
                        GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontType, new GridBagConstraints(1, 0, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        panel.add(new JLabel(fontSizeTitle), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        panel.add(fontSize, new GridBagConstraints(1, 1, 1, 1, 1, 1,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 10, 10), 0, 0));
        panel.add(new JLabel(fontStyleTitle), new GridBagConstraints(0, 2, 1, 1, 1, 1,
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
        exampleField.setText("Аа Яя Aa Zz");
    }

}
