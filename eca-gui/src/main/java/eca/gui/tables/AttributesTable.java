/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.dictionary.AttributesTypesDictionary;
import eca.filter.ConstantAttributesFilter;
import eca.filter.FilterDictionary;
import eca.gui.GuiUtils;
import eca.gui.dictionary.CommonDictionary;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.AttributesTableModel;
import eca.gui.text.DoubleDocument;
import eca.text.NumericFormatFactory;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
@Slf4j
public class AttributesTable extends JDataTableBase {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

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
    private static final int MIN_NUM_CLASS_VALUES = 2;
    private static final String CONSTANT_ATTR_ERROR_MESSAGE =
            "После удаления константных атрибутов не осталось ни одного входного атрибута!";
    private static final int INDEX_COLUMN_PREFERRED_WIDTH = 50;
    private static final String NUMERIC_OVERFLOW_ERROR_FORMAT =
            "Для числового атрибута '%s' найдены слишком большие значения!\nДлина целой части не должна превышать %d знаков!";

    private final ConstantAttributesFilter constantAttributesFilter = new ConstantAttributesFilter();

    private final InstancesTable instancesTable;
    private final JComboBox<String> classBox;

    private int lastDataModificationCount;
    private int lastAttributesModificationCount;
    private int lastClassModificationCount;
    private int classModificationCount;
    private Instances lastCreatedInstances;

    public AttributesTable(InstancesTable instancesTable, final JComboBox<String> classBox) {
        super(new AttributesTableModel(instancesTable.data()));
        this.instancesTable = instancesTable;
        this.classBox = classBox;
        this.addClassAttributeListener();
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

        renameMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.EDIT_ICON)));

        renameMenu.addActionListener(evt -> {
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
        });

        popMenu.add(renameMenu);
        this.setAutoResizeOff(false);
    }

    /**
     * Creates filtered instances taking into selected attributes with assigned class attribute.
     * {@link ConstantAttributesFilter} is used for filtering instances.
     *
     * @param relationName - relation name
     * @return created instances
     * @throws Exception
     */
    public Instances createAndFilterData(String relationName) throws Exception {
        if (isInstancesModified()) {
            Instances newDataSet = createInstances(relationName);
            newDataSet.setClass(newDataSet.attribute(classBox.getSelectedItem().toString()));
            if (newDataSet.classAttribute().numValues() < MIN_NUM_CLASS_VALUES) {
                throw new IllegalArgumentException(FilterDictionary.BAD_NUMBER_OF_CLASSES_ERROR_TEXT);
            }
            Instances filterInstances = constantAttributesFilter.filterInstances(newDataSet);
            if (filterInstances.numAttributes() < MIN_NUMBER_OF_SELECTED_ATTRIBUTES) {
                throw new IllegalArgumentException(CONSTANT_ATTR_ERROR_MESSAGE);
            }
            updateLastCreatedInstances(filterInstances);
        }
        return lastCreatedInstances;
    }

    /**
     * Creates instances taking into selected attributes with no assigned class attribute.
     *
     * @param relationName - relation name
     * @return created instances
     * @throws Exception
     */
    public Instances createSimpleData(String relationName) throws Exception {
        if (isInstancesModified()) {
            Instances newDataSet = createInstances(relationName);
            updateLastCreatedInstances(newDataSet);
        }
        return lastCreatedInstances;
    }

    public void validateData(boolean validateClass) {
        if (instancesTable.getRowCount() == 0) {
            throw new IllegalArgumentException(EMPTY_DATA_ERROR_MESSAGE);
        }
        if (validateSelectedAttributesCount()) {
            throw new IllegalArgumentException(NOT_ENOUGH_ATTRS_ERROR_MESSAGE);
        }
        if (validateClass) {
            if (!isSelected(classIndex())) {
                throw new IllegalArgumentException(CLASS_NOT_SELECTED_ERROR_MESSAGE);
            }
            if (isNumeric(classIndex())) {
                throw new IllegalArgumentException(BAD_CLASS_TYPE_ERROR_MESSAGE);
            }
        }
        validateValues();
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

    private void addClassAttributeListener() {
        classBox.addActionListener(event -> classModificationCount++);
    }

    private Instances createInstances(String relationName) throws ParseException {
        Instances newDataSet = new Instances(relationName, createAttributesList(), instancesTable.getRowCount());
        DecimalFormat format = instancesTable.getInstancesTableModel().format();
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
                    obj.setValue(attribute, valueAt.trim());
                }
            }
            newDataSet.add(obj);
        }
        return newDataSet;
    }

    private void updateLastCreatedInstances(Instances newInstances) {
        lastDataModificationCount = instancesTable.getInstancesTableModel().getModificationCount();
        lastAttributesModificationCount = getAttributesTableModel().getModificationCount();
        lastClassModificationCount = classModificationCount;
        lastCreatedInstances = newInstances;
    }

    private boolean isDataModified() {
        return lastDataModificationCount != instancesTable.getInstancesTableModel().getModificationCount();
    }

    private boolean isAttributesModified() {
        return lastAttributesModificationCount != getAttributesTableModel().getModificationCount();
    }

    private boolean isClassModified() {
        return classModificationCount != lastClassModificationCount;
    }

    private boolean isInstancesModified() {
        return lastCreatedInstances == null || isDataModified() || isAttributesModified() || isClassModified();
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

    private void validateValues() {
        for (int j = 1; j < instancesTable.getColumnCount(); j++) {
            String attribute = instancesTable.getColumnName(j);
            int attrIndex = j - 1;
            if (isSelected(attrIndex)) {
                for (int k = 0; k < instancesTable.getRowCount(); k++) {
                    String str = (String) instancesTable.getValueAt(k, j);
                    if (str != null) {
                        if (isNumeric(attrIndex)) {
                            if (!str.matches(DoubleDocument.DOUBLE_FORMAT)) {
                                throw new IllegalArgumentException(
                                        String.format(INCORRECT_NUMERIC_VALUES_ERROR_FORMAT, attribute));
                            }
                            tryParseNumeric(attribute, str);
                        }
                        if (isDate(attrIndex)) {
                            tryParseDate(attribute, str);
                        }
                    }
                }
            }
        }
    }

    private void tryParseNumeric(String attribute, String val) {
        int delimiterIndex = val.lastIndexOf(NumericFormatFactory.DECIMAL_SEPARATOR);
        int length = delimiterIndex < 0 ? val.length() : delimiterIndex;
        if (length > CommonDictionary.MAXIMUM_INTEGER_DIGITS) {
            throw new IllegalArgumentException(
                    String.format(NUMERIC_OVERFLOW_ERROR_FORMAT, attribute, CommonDictionary.MAXIMUM_INTEGER_DIGITS));
        }
    }

    private void tryParseDate(String attribute, String val) {
        try {
            SIMPLE_DATE_FORMAT.parse(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(INCORRECT_DATE_VALUES_ERROR_FORMAT,
                    attribute, CONFIG_SERVICE.getApplicationConfig().getDateFormat()));
        }
    }

    private ArrayList<Attribute> createAttributesList() {
        ArrayList<Attribute> attr = new ArrayList<>(instancesTable.getColumnCount() - 1);
        for (int i = 1; i < instancesTable.getColumnCount(); i++) {
            String attribute = instancesTable.getColumnName(i);
            int attrIndex = i - 1;
            if (isSelected(attrIndex)) {
                if (isNumeric(attrIndex)) {
                    attr.add(new Attribute(attribute));
                } else if (isDate(attrIndex)) {
                    attr.add(new Attribute(attribute, CONFIG_SERVICE.getApplicationConfig().getDateFormat()));
                } else {
                    attr.add(createNominalAttribute(attribute));
                }
            }
        }
        return attr;
    }

    private Attribute createNominalAttribute(String attribute) {
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
