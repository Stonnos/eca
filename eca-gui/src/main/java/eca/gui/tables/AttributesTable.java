/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.dictionary.AttributesTypesDictionary;
import eca.gui.Cleanable;
import eca.gui.GuiUtils;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.AttributesTableModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author Roman Batygin
 */
@Slf4j
public class AttributesTable extends JDataTableBase implements Cleanable {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String RENAME_ATTR_MENU_TEXT = "Переименовать атрибут";
    private static final String ATTR_NAME_TEXT = "Имя:";
    private static final String NEW_ATTR_NAME_FORMAT = "Новое имя атрибута: %s";
    private static final String DUPLICATE_ATTR_ERROR_MESSAGE_FORMAT = "Атрибут с именем '%s' уже существует!";
    private static final int INDEX_COLUMN_PREFERRED_WIDTH = 50;

    public AttributesTable(InstancesTable instancesTable, final JComboBox<String> classBox) {
        super(new AttributesTableModel(instancesTable.data()));
        this.getColumnModel().getColumn(0).setPreferredWidth(INDEX_COLUMN_PREFERRED_WIDTH);
        this.getColumnModel().getColumn(0).setMaxWidth(INDEX_COLUMN_PREFERRED_WIDTH);
        this.getColumnModel().getColumn(0).setMinWidth(INDEX_COLUMN_PREFERRED_WIDTH);
        this.getColumnModel().getColumn(AttributesTableModel.EDIT_INDEX).setMaxWidth(20);
        JComboBox<String> types = new JComboBox<>();
        types.addItem(AttributesTypesDictionary.NOMINAL);
        types.addItem(AttributesTypesDictionary.NUMERIC);
        types.addItem(AttributesTypesDictionary.DATE);
        TableColumn col = this.getColumnModel().getColumn(AttributesTableModel.LIST_INDEX);
        col.setCellEditor(new JComboBoxEditor(types));
        col.setCellRenderer(new ComboBoxRenderer());

        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem renameMenu = new JMenuItem(RENAME_ATTR_MENU_TEXT);

        popMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                int i = getSelectedRow();
                renameMenu.setEnabled(i != -1);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // Not implemented
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Not implemented
            }
        });

        renameMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EDIT_ICON)));

        renameMenu.addActionListener(evt -> {
            int i = getSelectedRow();
            if (i != -1) {
                String attrNewName = (String) JOptionPane.showInputDialog(AttributesTable.this.getRootPane(),
                        ATTR_NAME_TEXT,
                        String.format(NEW_ATTR_NAME_FORMAT, instancesTable.data().attribute(i).name()),
                        JOptionPane.INFORMATION_MESSAGE, null,
                        null, instancesTable.data().attribute(i).name());
                if (attrNewName != null) {
                    String trimName = attrNewName.trim();
                    if (!StringUtils.isEmpty(trimName)) {
                        try {
                            getAttributesTableModel().renameAttribute(i, trimName);
                            instancesTable.getColumnModel().getColumn(i + 1).setHeaderValue(trimName);
                            instancesTable.getRootPane().repaint();
                            classBox.insertItemAt(trimName, i);
                            classBox.removeItemAt(i + 1);
                        } catch (Exception e) {
                            LoggerUtils.error(log, e);
                            JOptionPane.showMessageDialog(AttributesTable.this.getRootPane(),
                                    String.format(DUPLICATE_ATTR_ERROR_MESSAGE_FORMAT, trimName),
                                    null, JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });

        popMenu.add(renameMenu);
        this.setAutoResizeOff(false);
    }

    public void selectAllAttributes() {
        getAttributesTableModel().selectAllAttributes();
    }

    public void resetValues() {
        getAttributesTableModel().resetValues();
    }

    public boolean isSelected(int i) {
        return getAttributesTableModel().isAttributeSelected(i);
    }

    public AttributesTableModel getAttributesTableModel() {
        return (AttributesTableModel) this.getModel();
    }

    public boolean isNumeric(int i) {
        return getAttributesTableModel().isNumeric(i);
    }

    public boolean isDate(int i) {
        return getAttributesTableModel().isDate(i);
    }

    public int getAttributeType(int i) {
        if (isNumeric(i)) {
            return Attribute.NUMERIC;
        } else if (isDate(i)) {
            return Attribute.DATE;
        } else {
            return Attribute.NOMINAL;
        }
    }

    @Override
    public void clear() {
        getAttributesTableModel().clear();
    }

    /**
     * Combo box render for attribute type selection.
     */
    private class ComboBoxRenderer extends JComboBox<String>
            implements TableCellRenderer {

        ComboBoxRenderer() {
            this.addItem(AttributesTypesDictionary.NUMERIC);
            this.addItem(AttributesTypesDictionary.NOMINAL);
            this.addItem(AttributesTypesDictionary.DATE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setFont(new Font(AttributesTable.this.getFont().getName(), Font.BOLD,
                    AttributesTable.this.getFont().getSize()));
            this.setSelectedItem(value);
            return this;
        }

    }

    /**
     * Combo box editor render for attribute type selection.
     */
    private class JComboBoxEditor extends DefaultCellEditor {

        JComboBoxEditor(JComboBox<String> box) {
            super(box);
            this.setClickCountToStart(0);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            c.setFont(new Font(AttributesTable.this.getFont().getName(), Font.BOLD,
                    AttributesTable.this.getFont().getSize()));
            return c;
        }
    }

} //End of class AttributesTable
