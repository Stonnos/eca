/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.dictionary.AttributesTypesDictionary;
import eca.gui.GuiUtils;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.AttributesTableModel;
import eca.gui.text.DoubleDocument;
import eca.text.DateFormat;
import lombok.extern.slf4j.Slf4j;
import weka.core.*;

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

    private final InstancesTable table;

    public AttributesTable(InstancesTable table, final JComboBox<String> classBox) {
        super(new AttributesTableModel(table.data()));
        this.table = table;
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.getColumnModel().getColumn(0).setMaxWidth(50);
        this.getColumnModel().getColumn(0).setMinWidth(50);
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
        //-----------------------------------
        renameMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                int i = getSelectedRow();
                if (i != -1) {
                    String name = (String) JOptionPane.showInputDialog(AttributesTable.this.getRootPane(),
                            ATTR_NAME_TEXT,
                            String.format(NEW_ATTR_NAME_FORMAT, table.data().attribute(i).name()),
                            JOptionPane.INFORMATION_MESSAGE, null,
                            null, null);
                    if (name != null) {
                        String trimName = name.trim();
                        if (!trimName.isEmpty()) {
                            try {
                                table.data().renameAttribute(i, trimName);
                                model().fireTableRowsUpdated(i, i);
                                table.getColumnModel().getColumn(i + 1).setHeaderValue(trimName);
                                table.getRootPane().repaint();
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

    public final Instances createData() throws Exception {
        return makeData();
    }

    public final boolean notSelected() {
        int count = 0;
        for (int i = 0; i < getRowCount(); i++) {
            if (isSelected(i)) {
                count++;
            }
        }
        return count < MIN_NUMBER_OF_SELECTED_ATTRIBUTES;
    }

    public final void check() throws Exception {
        if (table.getRowCount() == 0) {
            throw new Exception(EMPTY_DATA_ERROR_MESSAGE);
        }
        if (notSelected()) {
            throw new Exception(NOT_ENOUGH_ATTRS_ERROR_MESSAGE);
        }
        if (isNumeric(data().classIndex())) {
            throw new Exception(BAD_CLASS_TYPE_ERROR_MESSAGE);
        }
        if (!isSelected(data().classIndex())) {
            throw new Exception(CLASS_NOT_SELECTED_ERROR_MESSAGE);
        }
        for (int j = 0; j < data().numAttributes(); j++) {
            Attribute a = data().attribute(j);
            if (isSelected(j)) {
                for (int k = 0; k < table.getRowCount(); k++) {
                    String str = (String) table.getValueAt(k, j + 1);
                    if (str != null) {
                        if (isNumeric(j) && !str.matches(DoubleDocument.DOUBLE_FORMAT)) {
                            throw new Exception(String.format(INCORRECT_NUMERIC_VALUES_ERROR_FORMAT, a.name()));
                        }
                        if (isDate(j)) {
                            try {
                                DateFormat.SIMPLE_DATE_FORMAT.parse(str);
                            } catch (Exception e) {
                                throw new Exception(String.format(INCORRECT_DATE_VALUES_ERROR_FORMAT,
                                        a.name(), DateFormat.DATE_FORMAT));
                            }
                        }
                    }
                }
            }
        }
    }

    public void selectAllAttributes() {
        model().selectAllAttributes();
    }

    public void resetValues() {
        model().resetValues();
    }

    private AttributesTableModel model() {
        return (AttributesTableModel) this.getModel();
    }

    public Instances data() {
        return model().data();
    }

    public boolean isSelected(int i) {
        return model().isAttributeSelected(i);
    }

    public boolean isNumeric(int i) {
        return model().isNumeric(i);
    }

    public boolean isDate(int i) {
        return model().isDate(i);
    }

    private ArrayList<Attribute> makeAttributes() throws Exception {
        ArrayList<Attribute> attr = new ArrayList<>(data().numAttributes());
        for (int i = 0; i < data().numAttributes(); i++) {
            Attribute a = data().attribute(i);
            if (isSelected(i)) {
                if (isNumeric(a.index())) {
                    attr.add(new Attribute(a.name()));
                } else if (isDate(a.index())) {
                    attr.add(new Attribute(a.name(), DateFormat.DATE_FORMAT));
                } else {
                    attr.add(makeNominalAttribute(a));
                }
            }
        }
        return attr;
    }

    private Attribute makeNominalAttribute(Attribute a) throws Exception {
        ArrayList<String> values = new ArrayList<>();
        for (int j = 0; j < table.getRowCount(); j++) {
            String x = (String) table.getValueAt(j, a.index() + 1);
            if (x != null && !values.contains(x)) {
                values.add(x);
            }
        }

        return new Attribute(a.name(), values);
    }

    private Instances makeData() throws Exception {
        Instances data = data();
        Instances dataSet = new Instances(data.relationName(),
                makeAttributes(), table.getRowCount());
        DecimalFormat format = table.model().format();
        for (int i = 0; i < table.getRowCount(); i++) {
            Instance obj = new DenseInstance(dataSet.numAttributes());
            obj.setDataset(dataSet);
            for (int j = 0; j < dataSet.numAttributes(); j++) {
                Attribute a = dataSet.attribute(j);
                String str = (String) table.getValueAt(i,
                        data.attribute(a.name()).index() + 1);
                if (str == null) {
                    obj.setValue(a, Utils.missingValue());
                } else if (a.isDate()) {
                    obj.setValue(a, a.parseDate(str));
                } else if (a.isNumeric()) {
                    obj.setValue(a, format.parse(str).doubleValue());
                } else {
                    obj.setValue(a, str);
                }
            }
            dataSet.add(obj);
        }
        dataSet.setClass(dataSet.attribute(data.classAttribute().name()));

        return dataSet;
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
