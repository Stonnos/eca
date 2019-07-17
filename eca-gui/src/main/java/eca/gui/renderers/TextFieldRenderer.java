package eca.gui.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Text field renderer.
 *
 * @author Roman Batygin
 */
public class TextFieldRenderer extends JTextField implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.setText(value.toString());
        this.setBorder(null);
        return this;
    }

}
