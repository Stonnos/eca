package eca.gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Roman Batygin
 */

public class JButtonRenderer extends JButton
        implements TableCellRenderer {

    private final String text;

    public JButtonRenderer(String text) {
        this.text = text;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
        this.setFont(new Font(table.getFont().getName(), Font.BOLD,
                table.getFont().getSize()));
        this.setText(text);
        return this;
    }

}
