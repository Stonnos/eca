package eca.gui.editors;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Implements custom cell editor.
 *
 * @author Roman Batygin
 */
public class CustomCellEditor implements TableCellEditor {

    private JTable table;
    private TableCellEditor defaultTableCellEditor;

    private TableCellEditor tableCellEditor;
    private Map<Integer, TableCellEditor> editorMap = new HashMap<>();

    public CustomCellEditor(JTable table, TableCellEditor defaultTableCellEditor) {
        Objects.requireNonNull(table, "Table isn't specified!");
        Objects.requireNonNull(defaultTableCellEditor, "Default cell editor must be specified!");
        this.table = table;
        this.defaultTableCellEditor = defaultTableCellEditor;
    }

    public void setEditorAt(int row, TableCellEditor editor) {
        editorMap.put(row, editor);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return tableCellEditor.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return tableCellEditor.getCellEditorValue();
    }

    @Override
    public boolean stopCellEditing() {
        return tableCellEditor.stopCellEditing();
    }

    public void cancelCellEditing() {
        tableCellEditor.cancelCellEditing();
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        selectEditor((MouseEvent) anEvent);
        return tableCellEditor.isCellEditable(anEvent);
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        tableCellEditor.addCellEditorListener(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        tableCellEditor.removeCellEditorListener(l);
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        selectEditor((MouseEvent) anEvent);
        return tableCellEditor.shouldSelectCell(anEvent);
    }

    private void selectEditor(MouseEvent e) {
        int row = Optional.ofNullable(e).map(e1 -> table.rowAtPoint(e1.getPoint())).orElse(
                table.getSelectionModel().getAnchorSelectionIndex());
        tableCellEditor = Optional.ofNullable(editorMap.get(row)).orElse(defaultTableCellEditor);
    }
}
