/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;


import eca.buffer.StringCopier;
import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.gui.dialogs.JFontChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Basic class for all tables.
 *
 * @author Roman Batygin
 */
public class JDataTableBase extends JTable {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String FONT_SELECTION_MENU_TEXT = "Выбор шрифта";
    private static final String AUTO_SIZE_MENU_TEXT = "Автомасштабирование";
    private static final String DATA_COPY_MENU_TEXT = "Копировать данные";
    private static final String ALL_DATA_COPY_MENU_TEXT = "Копировать данные вместе с заголовком";
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final int WIDTH_SHIFT = 10;
    private static final int COLUMN_MIN_WIDTH = 15;
    private static final int INDEX_COLUMN = 0;
    private static final int ROW_HEIGHT_SHIFT = 6;
    private static final Color BORDER_COLOR = new Color(133, 133, 133);
    private static final Color HEADER_BACKGROUND_COLOR = new Color(192, 192, 192);
    private static final Color BACKGROUND_COLOR = new Color(224, 224, 224);

    private JCheckBoxMenuItem resizeMenu;

    public JDataTableBase(Object[][] data, Object[] title) {
        super(data, title);
        createView();
    }

    public final void setAutoResizeOff(boolean flag) {
        setAutoResizeOffMode(flag);
        resizeMenu.setSelected(!flag);
    }

    public JDataTableBase(TableModel model) {
        super(model);
        this.createView();
    }

    public JDataTableBase() {
        this.createView();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        FontMetrics metric = getGraphics().getFontMetrics(getTableHeader().getFont());
        for (int i = 0; i < getColumnCount(); i++) {
            if (!resizeMenu.getState()) {
                getColumnModel().getColumn(i).setMinWidth(getPreferredWidth(i, metric));

            } else {
                getColumnModel().getColumn(i).setMinWidth(COLUMN_MIN_WIDTH);
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
        int max = metric.stringWidth(this.getColumnName(column)) + WIDTH_SHIFT;
        for (int i = 0; i < this.getRowCount(); i++) {
            Object val = this.getValueAt(i, column);
            if (val != null) {
                max = Integer.max(max, metric.stringWidth(val.toString()) + WIDTH_SHIFT);
            }
        }
        return max;
    }

    private void createPopupMenu() {
        JPopupMenu popMenu = new JPopupMenu();
        JMenuItem fontMenu = new JMenuItem(FONT_SELECTION_MENU_TEXT);
        fontMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.FONT_ICON)));

        fontMenu.addActionListener(e -> {
            JFontChooser chooser = new JFontChooser(null, JDataTableBase.this.getFont());
            chooser.setVisible(true);
            if (chooser.dialogResult()) {
                Font font = chooser.getSelectedFont();
                JDataTableBase.this.font(font);
            }
            chooser.dispose();
        });
        resizeMenu = new JCheckBoxMenuItem(AUTO_SIZE_MENU_TEXT);
        resizeMenu.addItemListener(e -> setAutoResizeOffMode(!resizeMenu.getState()));
        //-----------------------------------
        JMenuItem copyMenu = new JMenuItem(DATA_COPY_MENU_TEXT);
        copyMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COPY_ICON)));
        JMenuItem copyWithHeaderMenu = new JMenuItem(ALL_DATA_COPY_MENU_TEXT);
        copyWithHeaderMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.COPY_ICON)));

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

    private void createView() {
        if (getColumnCount() > 0) {
            this.getColumnModel().getColumn(INDEX_COLUMN).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus, int row,
                                                               int column) {
                    Component comp = super.getTableCellRendererComponent(table, value,
                            isSelected, hasFocus, row, column);
                    comp.setBackground(BACKGROUND_COLOR);
                    return comp;
                }
            });
        }
        this.getTableHeader().setReorderingAllowed(false);
        this.getTableHeader().setBackground(HEADER_BACKGROUND_COLOR);
        this.getTableHeader().setBorder(BorderFactory.
                createEtchedBorder(BORDER_COLOR, null));
        this.createPopupMenu();
        this.font(DEFAULT_FONT);
        this.setAutoResizeOff(true);
    }

    private void font(Font font) {
        this.setFont(font);
        this.setRowHeight(this.getFont().getSize() + ROW_HEIGHT_SHIFT);
        this.getTableHeader().setFont(new Font(font.getName(), Font.BOLD, font.getSize() + 2));
    }

    /**
     * Implements copying table into system clipboard.
     */
    private class JTableClipboard {

        StringCopier stringCopier = new StringCopier();

        void copy(boolean copyHeaders) {
            stringCopier.setCopyString(JTableConverter.convertToString(JDataTableBase.this, copyHeaders));
            stringCopier.copy();
        }
    }

}
