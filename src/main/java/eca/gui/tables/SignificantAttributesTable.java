/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.tables.models.SignificantAttributesTableModel;
import eca.roc.AttributesSelection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Рома
 */
public class SignificantAttributesTable extends JDataTableBase {

    public SignificantAttributesTable(AttributesSelection roc, int digits) {
        super(new SignificantAttributesTableModel(roc, digits));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                int i = row >= roc.data().classIndex() ? row + 1 : row;
                if (roc.isSignificant(i)) {
                    cell.setForeground(Color.RED);
                } else {
                    cell.setForeground(table.getForeground());
                }
                return cell;
            }
        };

        for (int i = 0; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

}
