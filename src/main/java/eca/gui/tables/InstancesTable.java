/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.dialogs.JTextFieldMatrixDialog;
import eca.gui.tables.models.InstancesTableModel;
import org.apache.commons.lang3.StringUtils;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Roman Batygin
 */
public class InstancesTable extends JDataTableBase {

    private static final String DELETE_ATTR_MENU_TEXT = "Удалить выбранный объект";
    private static final String DELETE_ATTRS_MENU_TEXT = "Удалить выбранные объекты";
    private static final String ADD_INSTANCE_MENU_TEXT = "Добавить объект";
    private static final String CLEAR_DATA_MENU_TEXT = "Очистка";
    private static final String DELETE_MISSING_VALUES_MENU_TEXT = "Удалить объекты с пропусками";
    private static final String REPLACE_ATTRS_VALUES_MENU_TEXT = "Замена значений атрибута";
    private static final String ARE_YOU_SURE_TEXT = "Вы уверены?";
    private static final String INSTANCE_VALUE_TEXT = "Значения объекта:";
    private static final String ADD_INSTANCE_TEXT = "Добавление объекта";
    private static final String OLD_VALUE_TEXT = "Старое значение:";
    private static final String NEW_VALUE_TEXT = "Новое значение:";
    private static final String REPLACE_VALUE_TEXT = "Замена значения";

    public InstancesTable(final Instances data, final JTextField numInstances) {
        super(new InstancesTableModel(data));
        MissingCellRenderer renderer = new MissingCellRenderer();
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        //----------------------------------------
        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem deleteMenu = new JMenuItem(DELETE_ATTR_MENU_TEXT);
        JMenuItem deleteAllMenu = new JMenuItem(DELETE_ATTRS_MENU_TEXT);
        JMenuItem insertMenu = new JMenuItem(ADD_INSTANCE_MENU_TEXT);
        JMenuItem clearMenu = new JMenuItem(CLEAR_DATA_MENU_TEXT);
        JMenuItem missMenu = new JMenuItem(DELETE_MISSING_VALUES_MENU_TEXT);
        JMenuItem reValueMenu = new JMenuItem(REPLACE_ATTRS_VALUES_MENU_TEXT);
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
                        INSTANCE_VALUE_TEXT,
                        ADD_INSTANCE_TEXT, JOptionPane.INFORMATION_MESSAGE, null,
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
                        ARE_YOU_SURE_TEXT, null,
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
                    String value = getValueAt(j, i) != null ? getValueAt(j, i).toString() : StringUtils.EMPTY;
                    String[] labels = {OLD_VALUE_TEXT,
                            NEW_VALUE_TEXT};
                    String[] values = {value, StringUtils.EMPTY};
                    JTextFieldMatrixDialog frame = new JTextFieldMatrixDialog(null,
                            REPLACE_VALUE_TEXT, labels, values,
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
        this.getTableHeader().setComponentPopupMenu(popMenu);
        ;
    }

    public final InstancesTableModel model() {
        return (InstancesTableModel) this.getModel();
    }

    public final Instances data() {
        return model().data();
    }

}
