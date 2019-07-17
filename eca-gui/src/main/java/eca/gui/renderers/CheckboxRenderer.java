package eca.gui.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Checkbox renderer.
 *
 * @author Roman Batygin
 */
public class CheckboxRenderer extends JCheckBox implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.setSelected(Boolean.valueOf(value.toString()));
        return this;
    }

}
