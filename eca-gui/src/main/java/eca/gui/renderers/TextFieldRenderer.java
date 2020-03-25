package eca.gui.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Optional;

/**
 * Text field renderer.
 *
 * @author Roman Batygin
 */
public class TextFieldRenderer extends JTextField implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.setText(Optional.ofNullable(value).map(Object::toString).orElse(null));
        this.setBorder(null);
        return this;
    }

}
