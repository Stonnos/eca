package eca.gui.renderers;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Roman Batygin
 */
public class CustomCellRenderer implements TableCellRenderer {

    private HashMap<Integer, TableCellRenderer> rendererMap = new HashMap<>();

    private TableCellRenderer defaultRenderer;

    public CustomCellRenderer(TableCellRenderer defaultRenderer) {
        Objects.requireNonNull(defaultRenderer, "Default renderer isn't specified!");
        this.defaultRenderer = defaultRenderer;
    }

    public void setRendererAt(int row, TableCellRenderer renderer) {
        rendererMap.put(row, renderer);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        TableCellRenderer renderer = Optional.ofNullable(rendererMap.get(row)).orElse(defaultRenderer);
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
