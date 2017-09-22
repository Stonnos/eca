/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Roman Batygin
 */
public class MissingCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component comp = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        if (value == null) {
            comp.setBackground(Color.YELLOW);
        } else {
            comp.setBackground(Color.WHITE);
        }
        return comp;
    }
}
