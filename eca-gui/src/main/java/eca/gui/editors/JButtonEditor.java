package eca.gui.editors;

import eca.gui.GuiUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author Roman Batygin
 */

public abstract class JButtonEditor extends DefaultCellEditor {

    private static final Cursor HAND_CURSOR = new Cursor(Cursor.HAND_CURSOR);

    private JButton button;
    private boolean isPushed;

    protected JButtonEditor(String text) {
        super(new JCheckBox());
        this.setClickCountToStart(0);
        button = new JButton(text);
        button.setOpaque(true);
        button.setCursor(HAND_CURSOR);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        GuiUtils.updateForegroundAndBackGround(button, table, isSelected);
        button.setFont(new Font(table.getFont().getName(), Font.BOLD,
                table.getFont().getSize()));
        doOnPushing(table, value, isSelected, row, column);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            doAfterPushing();
        }
        isPushed = false;
        return button.getText();
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    protected abstract void doOnPushing(JTable table, Object value,
                                        boolean isSelected, int row, int column);

    protected abstract void doAfterPushing();

}
