/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;


import eca.gui.dialogs.JFontChooser;
import eca.io.buffer.StringCopier;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author Roman Batygin
 */
public class JDataTableBase extends JTable {

    private static final String FONT_SELECTION_MENU_TEXT = "Выбор шрифта";
    private static final String AUTO_SIZE_MENU_TEXT = "Автомасштабирование";
    private static final String DATA_COPY_MENU_TEXT = "Копировать данные";
    private static final String ALL_DATA_COPY_MENU_TEXT = "Копировать данные вместе с заголовком";
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);

    private JCheckBoxMenuItem resizeMenu;

    public JDataTableBase(Object[][] data, Object[] title) {
        super(data, title);
        makeView();
    }

    public final void setAutoResizeOff(boolean flag) {
        setAutoResizeOffMode(flag);
        resizeMenu.setSelected(!flag);
    }

    public JDataTableBase(TableModel model) {
        super(model);
        this.makeView();
    }

    public JDataTableBase() {
        this.makeView();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        FontMetrics metric = getGraphics().getFontMetrics(getTableHeader().getFont());
        for (int i = 0; i < getColumnCount(); i++) {
            if (!resizeMenu.getState()) {
                getColumnModel().getColumn(i).setMinWidth(getPreferredWidth(i, metric));

            } else {
                getColumnModel().getColumn(i).setMinWidth(15);
            }
        }
    }

    private void setAutoResizeOffMode(boolean flag) {
        if (flag) {
            this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } else {
            this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }
    }

    private int getPreferredWidth(int column, FontMetrics metric) {
        int max = metric.stringWidth(this.getColumnName(column)) + 10;
        for (int i = 0; i < this.getRowCount(); i++) {
            Object val = this.getValueAt(i, column);
            if (val != null) {
                max = Integer.max(max, metric.stringWidth(val.toString()) + 10);
            }
        }
        return max;
    }

    private void createPopupMenu() {
        JPopupMenu popMenu = new JPopupMenu();
        JMenuItem fontMenu = new JMenuItem(FONT_SELECTION_MENU_TEXT);

        fontMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                JFontChooser chooser = new JFontChooser(null, JDataTableBase.this.getFont());
                chooser.setVisible(true);
                if (chooser.dialogResult()) {
                    Font font = chooser.getSelectedFont();
                    JDataTableBase.this.font(font);
                }
                chooser.dispose();
            }
        });
        resizeMenu = new JCheckBoxMenuItem(AUTO_SIZE_MENU_TEXT);
        resizeMenu.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent evt) {
                setAutoResizeOffMode(!resizeMenu.getState());
            }
        });
        //-----------------------------------
        JMenuItem copyMenu = new JMenuItem(DATA_COPY_MENU_TEXT);
        JMenuItem copyWithHeaderMenu = new JMenuItem(ALL_DATA_COPY_MENU_TEXT);

        copyMenu.addActionListener(new ActionListener() {

            JTableClipboard jTableClipboard = new JTableClipboard();

            @Override
            public void actionPerformed(ActionEvent evt) {
                jTableClipboard.copy(false);
            }
        });

        copyWithHeaderMenu.addActionListener(new ActionListener() {

            JTableClipboard jTableClipboard = new JTableClipboard();

            @Override
            public void actionPerformed(ActionEvent evt) {
                jTableClipboard.copy(true);
            }
        });

        popMenu.add(fontMenu);
        popMenu.add(resizeMenu);
        popMenu.add(copyMenu);
        popMenu.add(copyWithHeaderMenu);
        this.setComponentPopupMenu(popMenu);
    }

    private void makeView() {
        if (getColumnCount() > 0) {
            this.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row,
                                                               int column) {
                    Component comp = super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                    comp.setBackground(new Color(224, 224, 224));
                    return comp;
                }
            });
        }
        this.getTableHeader().setReorderingAllowed(false);
        this.getTableHeader().setBackground(new Color(192, 192, 192));
        this.getTableHeader().setBorder(BorderFactory.
                createEtchedBorder(new Color(133, 133, 133), null));
        this.createPopupMenu();
        this.font(DEFAULT_FONT);
        this.setAutoResizeOff(true);
    }

    private void font(Font font) {
        this.setFont(font);
        this.setRowHeight(this.getFont().getSize() + 6);
        this.getTableHeader().setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
    }

    /**
     * Implements copying table into system clipboard.
     */
    private class JTableClipboard {

        StringCopier stringCopier;

        void copy(boolean copyHeaders) {
            if (stringCopier == null) {
                stringCopier = new StringCopier();
            }
            stringCopier.setCopyString(JTableConverter.convertToString(JDataTableBase.this, copyHeaders));
            stringCopier.copy();
        }
    }

}
