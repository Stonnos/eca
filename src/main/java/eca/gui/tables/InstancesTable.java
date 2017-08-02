/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.dialogs.JOptionPaneN;
import eca.gui.tables.models.InstancesTableModel;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Рома
 */
public class InstancesTable extends JDataTableBase {
    
    public InstancesTable(final Instances data, final JTextField numInstances) {
        super(new InstancesTableModel(data));
        MissingCellRenderer renderer = new MissingCellRenderer();
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        //----------------------------------------
        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem deleteMenu = new JMenuItem("Удалить выбранный объект");
        JMenuItem deleteAllMenu = new JMenuItem("Удалить выбранные объекты");
        JMenuItem insertMenu = new JMenuItem("Добавить объект");
        JMenuItem clearMenu = new JMenuItem("Очистка");
        JMenuItem missMenu = new JMenuItem("Удалить объекты с пропусками");
        JMenuItem reValueMenu = new JMenuItem("Замена значений атрибута");
        //-----------------------------------
        popMenu.addPopupMenuListener(new PopupMenuListener() {
            
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                int i = getSelectedRow();
                deleteMenu.setEnabled(i != -1);
                deleteAllMenu.setEnabled(i != -1);
                missMenu.setEnabled(getRowCount() != 0);
                reValueMenu.setEnabled(getSelectedColumn() > 0);
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                
            }
            
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                
            }
        });
        //-----------------------------------
        deleteMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = getSelectedRow();
                if (i != -1) {
                    model().remove(i);
                }
                numInstances.setText(String.valueOf(model().getRowCount()));
            }
        });
        //-----------------------------------
        deleteAllMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int[] i = getSelectedRows();
                if (i.length != 0) {
                    model().remove(i);
                }
                numInstances.setText(String.valueOf(model().getRowCount()));
            }
        });
        //-----------------------------------
        insertMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String val = (String) JOptionPane.showInputDialog(InstancesTable.this.getRootPane(),
                        "Значения объекта:",
                        "Добавление объекта", JOptionPane.INFORMATION_MESSAGE, null,
                        null, null);
                if (val != null) {
                    String obj = val.trim();
                    model().add(obj.isEmpty() ? null : obj);
                    numInstances.setText(String.valueOf(model().getRowCount()));
                }
            }
        });
        //-----------------------------------
        clearMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int result = JOptionPane.showConfirmDialog(InstancesTable.this.getRootPane(),
                        "Вы уверены?", null,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    model().clear();
                    numInstances.setText(String.valueOf(model().getRowCount()));
                }
            }
        });
        //-----------------------------------
        missMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                model().removeMissing();
                numInstances.setText(String.valueOf(model().getRowCount()));
            }
        });
        //-----------------------------------
        reValueMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = getSelectedColumn();
                if (i > 0) {
                    int j = getSelectedRow();
                    String value = getValueAt(j, i) != null ? getValueAt(j, i).toString() : "";
                    String[] labels = {"Старое значение:",
                            "Новое значение:"};
                    String[] values = {value , ""};
                    JOptionPaneN frame = new JOptionPaneN(null,
                            "Замена значения", labels, values,
                            2, 10);
                    frame.setVisible(true);
                    if (frame.dialogResult()) {
                        model().replace(i - 1, frame.valueAt(0), frame.valueAt(1));
                    }
                }
            }
        });
        //-----------------------------------
        popMenu.add(deleteMenu);
        popMenu.add(deleteAllMenu);
        popMenu.add(insertMenu);
        popMenu.add(clearMenu);
        popMenu.add(missMenu);
        popMenu.add(reValueMenu);
        this.getTableHeader().setComponentPopupMenu(popMenu);;
    }
    
    public final InstancesTableModel model() {
        return (InstancesTableModel) this.getModel();
    }
    
    public final Instances data() {
        return model().data();
    }
    
}
