/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.dictionary.AttributesTypesDictionary;
import eca.filter.ConstantAttributesFilter;
import eca.gui.GuiUtils;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.AttributesTableModel;
import eca.gui.text.DoubleDocument;
import eca.text.DateFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
@Slf4j
public class AttributesTable extends JDataTableBase {

    private static final String RENAME_ATTR_MENU_TEXT = "Переименовать атрибут";
    private static final String ATTR_NAME_TEXT = "Имя:";
    private static final String NEW_ATTR_NAME_FORMAT = "Новое имя атрибута: %s";
    private static final String DUPLICATE_ATTR_ERROR_MESSAGE_FORMAT = "Атрибут с именем '%s' уже существует!";
    private static final String EMPTY_DATA_ERROR_MESSAGE = "Необходимо заполнить таблицу с данными!";
    private static final String NOT_ENOUGH_ATTRS_ERROR_MESSAGE = "Выберите хотя бы 2 атрибута!";
    private static final String BAD_CLASS_TYPE_ERROR_MESSAGE = "Атрибут класса должен иметь категориальный тип!";
    private static final String CLASS_NOT_SELECTED_ERROR_MESSAGE = "Не выбран атрибут класса!";
    private static final String INCORRECT_NUMERIC_VALUES_ERROR_FORMAT = "Недопустимые значения числового атрибута %s!";
    private static final String INCORRECT_DATE_VALUES_ERROR_FORMAT =
            "Формат даты для атрибута '%s' должен быть следующим: %s";

    private static final int MIN_NUMBER_OF_SELECTED_ATTRIBUTES = 2;
    private static final String CONSTANT_ATTR_ERROR_MESSAGE =
            "После удаления константных атрибутов не осталось ни одного входного атрибута!";
    private static final int INDEX_COLUMN_PREFERRED_WIDTH = 50;

    private final ConstantAttributesFilter constantAttributesFilter = new ConstantAttributesFilter();

    private final InstancesTable instancesTable;
    private final JComboBox<String> classBox;

    private int lastDataModificationCount;
    private int lastAttributesModificationCount;
    private Instances lastCreatedInstances;

    public AttributesTable(InstancesTable instancesTable, final JComboBox<String> classBox) {
        super(new AttributesTableModel(instancesTable.data()));
        this.instancesTable = instancesTable;
        this.classBox = classBox;
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
        //-------------------------------------------------
        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem renameMenu = new JMenuItem(RENAME_ATTR_MENU_TEXT);
        //-----------------------------------
        popMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                int i = getSelectedRow();
                renameMenu.setEnabled(i != -1);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {

            }
        });

        renameMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = getSelectedRow();
                if (i != -1) {
                    String attrNewName = (String) JOptionPane.showInputDialog(AttributesTable.this.getRootPane(),
                            ATTR_NAME_TEXT,
                            String.format(NEW_ATTR_NAME_FORMAT, instancesTable.data().attribute(i).name()),
                            JOptionPane.INFORMATION_MESSAGE, null,
                            null, null);
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
            }
        });

        popMenu.add(renameMenu);
        this.setAutoResizeOff(false);
    }

    public Instances createData(String relationName) throws Exception {
        if (lastCreatedInstances == null ||
                (lastDataModificationCount != instancesTable.model().getModificationCount() ||
                        lastAttributesModificationCount != getAttributesTableModel().getModificationCount())) {
            Instances newDataSet = new Instances(relationName, createAttributesList(), instancesTable.getRowCount());
            DecimalFormat format = instancesTable.model().format();
            for (int i = 0; i < instancesTable.getRowCount(); i++) {
                Instance obj = new DenseInstance(newDataSet.numAttributes());
                obj.setDataset(newDataSet);
                for (int j = 0; j < newDataSet.numAttributes(); j++) {
                    Attribute attribute = newDataSet.attribute(j);
                    String valueAt = (String) instancesTable.getValueAt(i, getAttrIndex(attribute.name()));
                    if (valueAt == null) {
                        obj.setValue(attribute, Utils.missingValue());
                    } else if (attribute.isDate()) {
                        obj.setValue(attribute, attribute.parseDate(valueAt));
                    } else if (attribute.isNumeric()) {
                        obj.setValue(attribute, format.parse(valueAt).doubleValue());
                    } else {
                        obj.setValue(attribute, valueAt);
                    }
                }
                newDataSet.add(obj);
            }
            newDataSet.setClass(newDataSet.attribute(classBox.getSelectedItem().toString()));
            Instances filterInstances = constantAttributesFilter.filterInstances(newDataSet);
            if (filterInstances.numAttributes() < MIN_NUMBER_OF_SELECTED_ATTRIBUTES) {
                throw new Exception(CONSTANT_ATTR_ERROR_MESSAGE);
            }
            lastDataModificationCount = instancesTable.model().getModificationCount();
            lastAttributesModificationCount = getAttributesTableModel().getModificationCount();
            lastCreatedInstances = filterInstances;
        }
        return lastCreatedInstances;
    }

    public void validateData() throws Exception {
        if (instancesTable.getRowCount() == 0) {
            throw new Exception(EMPTY_DATA_ERROR_MESSAGE);
        }
        if (validateSelectedAttributesCount()) {
            throw new Exception(NOT_ENOUGH_ATTRS_ERROR_MESSAGE);
        }
        if (!isSelected(classIndex())) {
            throw new Exception(CLASS_NOT_SELECTED_ERROR_MESSAGE);
        }
        if (isNumeric(classIndex())) {
            throw new Exception(BAD_CLASS_TYPE_ERROR_MESSAGE);
        }
        for (int j = 1; j < instancesTable.getColumnCount(); j++) {
            String attribute = instancesTable.getColumnName(j);
            int attrIndex = j - 1;
            if (isSelected(attrIndex)) {
                for (int k = 0; k < instancesTable.getRowCount(); k++) {
                    String str = (String) instancesTable.getValueAt(k, j);
                    if (str != null) {
                        if (isNumeric(attrIndex) && !str.matches(DoubleDocument.DOUBLE_FORMAT)) {
                            throw new Exception(String.format(INCORRECT_NUMERIC_VALUES_ERROR_FORMAT, attribute));
                        }
                        if (isDate(attrIndex)) {
                            try {
                                DateFormat.SIMPLE_DATE_FORMAT.parse(str);
                            } catch (Exception e) {
                                throw new Exception(String.format(INCORRECT_DATE_VALUES_ERROR_FORMAT,
                                        attribute, DateFormat.DATE_FORMAT));
                            }
                        }
                    }
                }
            }
        }
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

    private AttributesTableModel getAttributesTableModel() {
        return (AttributesTableModel) this.getModel();
    }

    private boolean isNumeric(int i) {
        return getAttributesTableModel().isNumeric(i);
    }

    private boolean isDate(int i) {
        return getAttributesTableModel().isDate(i);
    }

    private boolean validateSelectedAttributesCount() {
        int count = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if (isSelected(i)) {
                count++;
            }
        }
        return count < MIN_NUMBER_OF_SELECTED_ATTRIBUTES;
    }

    private int getAttrIndex(String name) {
        return instancesTable.getTableHeader().getColumnModel().getColumnIndex(name);
    }

    private int classIndex() {
        return classBox.getSelectedIndex();
    }

    private ArrayList<Attribute> createAttributesList() throws Exception {
        ArrayList<Attribute> attr = new ArrayList<>(instancesTable.getColumnCount() - 1);
        for (int i = 1; i < instancesTable.getColumnCount(); i++) {
            String attribute = instancesTable.getColumnName(i);
            int attrIndex = i - 1;
            if (isSelected(attrIndex)) {
                if (isNumeric(attrIndex)) {
                    attr.add(new Attribute(attribute));
                } else if (isDate(attrIndex)) {
                    attr.add(new Attribute(attribute, DateFormat.DATE_FORMAT));
                } else {
                    attr.add(createNominalAttribute(attribute));
                }
            }
        }
        return attr;
    }

    private Attribute createNominalAttribute(String attribute) throws Exception {
        ArrayList<String> values = new ArrayList<>();
        for (int j = 0; j < instancesTable.getRowCount(); j++) {
            String stringValue = (String) instancesTable.getValueAt(j, getAttrIndex(attribute));
            if (stringValue != null) {
                String trimValue = stringValue.trim();
                if (!StringUtils.isEmpty(trimValue) && !values.contains(trimValue)) {
                    values.add(stringValue.trim());
                }
            }
        }

        return new Attribute(attribute, values);
    }

    /**
     *
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

    } // End of class ComboBoxRender

    /**
     *
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
