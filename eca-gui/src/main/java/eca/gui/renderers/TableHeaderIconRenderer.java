package eca.gui.renderers;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static eca.gui.tables.JDataTableBase.HEADER_BACKGROUND_COLOR;

public class TableHeaderIconRenderer extends DefaultTableCellRenderer {
    private static final int ICON_TEXT_GAP = 5;

    @Getter
    @Setter
    private Icon icon;

    @Getter
    @Setter
    private Font labelFont;

    public TableHeaderIconRenderer() {
        // Center text and icon
        setHorizontalAlignment(JLabel.CENTER);
        setVerticalAlignment(JLabel.CENTER);
        // Adjust gap between icon and text
        setIconTextGap(ICON_TEXT_GAP);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setIcon(icon);
        label.setBackground(HEADER_BACKGROUND_COLOR);
        label.setFont(labelFont);
        return label;
    }
}
