/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.config.ConfigurationService;
import eca.config.IconType;
import eca.filter.ConstantAttributesFilter;
import eca.filter.FilterDictionary;
import eca.gui.renderers.MissingCellRenderer;
import eca.gui.dialogs.CreateNewInstanceDialog;
import eca.gui.dialogs.JTextFieldMatrixDialog;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.InstancesTableModel;
import eca.gui.text.DoubleDocument;
import eca.util.Entry;
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
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static eca.gui.service.ValidationService.isNumericOverflow;
import static eca.gui.service.ValidationService.parseDate;

/**
 * @author Roman Batygin
 */
@Slf4j
public class InstancesTable extends JDataTableBase {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final String DELETE_ATTR_MENU_TEXT = "Удалить выбранный объект";
    private static final String DELETE_ATTRS_MENU_TEXT = "Удалить выбранные объекты";
    private static final String ADD_INSTANCE_MENU_TEXT = "Добавить объект";
    private static final String CLEAR_DATA_MENU_TEXT = "Очистка";
    private static final String DELETE_MISSING_VALUES_MENU_TEXT = "Удалить объекты с пропусками";
    private static final String REPLACE_ATTRS_VALUES_MENU_TEXT = "Замена значений атрибута";
    private static final String ARE_YOU_SURE_TEXT = "Вы уверены?";
    private static final String OLD_VALUE_TEXT = "Старое значение:";
    private static final String NEW_VALUE_TEXT = "Новое значение:";
    private static final String REPLACE_VALUE_TEXT = "Замена значения";
    private static final int ROWS = 2;
    private static final int TEXT_FIELD_LENGTH = 10;

    private static final String EMPTY_DATA_ERROR_MESSAGE = "Необходимо заполнить таблицу с данными!";
    private static final String NOT_ENOUGH_ATTRS_ERROR_MESSAGE = "Выберите хотя бы 2 атрибута!";
    private static final String BAD_CLASS_TYPE_ERROR_MESSAGE = "Атрибут класса должен иметь категориальный тип!";
    private static final String CLASS_NOT_SELECTED_ERROR_MESSAGE = "Не выбран атрибут класса!";
    private static final String INCORRECT_NUMERIC_VALUES_ERROR_FORMAT = "Недопустимые значения числового атрибута %s!";

    private static final int MIN_NUMBER_OF_SELECTED_ATTRIBUTES = 2;
    private static final int MIN_NUM_CLASS_VALUES = 2;
    private static final String CONSTANT_ATTR_ERROR_MESSAGE =
            "После удаления константных атрибутов не осталось ни одного входного атрибута!";

    private AttributesTable attributesTable;
    private JComboBox<String> classBox;

    private int lastDataModificationCount;
    private int lastAttributesModificationCount;
    private int lastClassModificationCount;
    private int classModificationCount;
    private Instances lastCreatedInstances;

    private final ConstantAttributesFilter constantAttributesFilter = new ConstantAttributesFilter();

    public InstancesTable(Instances data,
                          JTextField numInstances,
                          JComboBox<String> classBox,
                          int digits) {
        super(new InstancesTableModel(data, digits));
        this.classBox = classBox;
        MissingCellRenderer renderer = new MissingCellRenderer();
        for (int i = 1; i < this.getColumnCount(); i++) {
            this.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        this.createPopupMenuList(numInstances);
        this.addClassAttributeListener();
        this.addSortListenerToHeader();
    }

    /**
     * Returns instances table model.
     *
     * @return instances table model
     */
    public InstancesTableModel getInstancesTableModel() {
        return (InstancesTableModel) this.getModel();
    }

    /**
     * Returns initial instances.
     *
     * @return initial instances
     */
    public Instances data() {
        return getInstancesTableModel().data();
    }

    public AttributesTable getAttributesTable() {
        return attributesTable;
    }

    public void setAttributesTable(AttributesTable attributesTable) {
        this.attributesTable = attributesTable;
    }

    private void addClassAttributeListener() {
        classBox.addActionListener(event -> classModificationCount++);
    }

    private void createPopupMenuList(final JTextField numInstances) {
        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem deleteMenu = new JMenuItem(DELETE_ATTR_MENU_TEXT);
        deleteMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DELETE_ICON)));
        JMenuItem deleteAllMenu = new JMenuItem(DELETE_ATTRS_MENU_TEXT);
        deleteAllMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DELETE_ALL_ICON)));
        JMenuItem insertMenu = new JMenuItem(ADD_INSTANCE_MENU_TEXT);
        insertMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.ADD_ICON)));
        JMenuItem clearMenu = new JMenuItem(CLEAR_DATA_MENU_TEXT);
        clearMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.CLEAR_ICON)));
        JMenuItem missMenu = new JMenuItem(DELETE_MISSING_VALUES_MENU_TEXT);
        missMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.DELETE_ALL_ICON)));
        JMenuItem reValueMenu = new JMenuItem(REPLACE_ATTRS_VALUES_MENU_TEXT);
        reValueMenu.setIcon(new ImageIcon(CONFIG_SERVICE.getIconUrl(IconType.REPLACE_ICON)));
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
                // Not implemented
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // Not implemented
            }
        });

        deleteMenu.addActionListener(e -> {
            int i = getSelectedRow();
            if (i != -1) {
                getInstancesTableModel().remove(i);
            }
            numInstances.setText(String.valueOf(getInstancesTableModel().getRowCount()));
        });

        deleteAllMenu.addActionListener(e -> {
            int[] i = getSelectedRows();
            if (i.length != 0) {
                getInstancesTableModel().remove(i);
            }
            numInstances.setText(String.valueOf(getInstancesTableModel().getRowCount()));
        });

        insertMenu.addActionListener(e -> {
            CreateNewInstanceDialog createNewInstanceDialog = new CreateNewInstanceDialog(null, createAttributesInfo());
            createNewInstanceDialog.setVisible(true);
            if (createNewInstanceDialog.isDialogResult()) {
                getInstancesTableModel().addRow(createNewInstanceDialog.getValues());
                numInstances.setText(String.valueOf(getInstancesTableModel().getRowCount()));
            }
        });

        clearMenu.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(InstancesTable.this.getRootPane(),
                    ARE_YOU_SURE_TEXT, CLEAR_DATA_MENU_TEXT,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                getInstancesTableModel().clear();
                numInstances.setText(String.valueOf(getInstancesTableModel().getRowCount()));
            }
        });

        missMenu.addActionListener(e -> {
            getInstancesTableModel().removeMissing();
            numInstances.setText(String.valueOf(getInstancesTableModel().getRowCount()));
        });

        reValueMenu.addActionListener(e -> {
            int i = getSelectedColumn();
            if (i > 0) {
                int j = getSelectedRow();
                String value = getValueAt(j, i) != null ? getValueAt(j, i).toString() : StringUtils.EMPTY;
                String[] labels = {OLD_VALUE_TEXT, NEW_VALUE_TEXT};
                String[] values = {value, StringUtils.EMPTY};
                JTextFieldMatrixDialog frame = new JTextFieldMatrixDialog(null, REPLACE_VALUE_TEXT,
                        labels, values, ROWS, TEXT_FIELD_LENGTH);
                frame.setVisible(true);
                if (frame.dialogResult()) {
                    getInstancesTableModel().replace(i - 1, frame.valueAt(0), frame.valueAt(1));
                }
            }
        });
        popMenu.add(deleteMenu);
        popMenu.add(deleteAllMenu);
        popMenu.add(insertMenu);
        popMenu.add(clearMenu);
        popMenu.add(missMenu);
        popMenu.add(reValueMenu);
        this.getTableHeader().setComponentPopupMenu(popMenu);
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
        return createInstances(relationName);
    }

    public void validateData(boolean validateClass) {
        if (getRowCount() == 0) {
            throw new IllegalArgumentException(EMPTY_DATA_ERROR_MESSAGE);
        }
        if (validateClass) {
            if (validateSelectedAttributesCount()) {
                throw new IllegalArgumentException(NOT_ENOUGH_ATTRS_ERROR_MESSAGE);
            }
            if (!attributesTable.isSelected(getClassIndex())) {
                throw new IllegalArgumentException(CLASS_NOT_SELECTED_ERROR_MESSAGE);
            }
            if (attributesTable.isNumeric(getClassIndex())) {
                throw new IllegalArgumentException(BAD_CLASS_TYPE_ERROR_MESSAGE);
            }
        }
        validateValues();
    }

    private void addSortListenerToHeader() {
        setColumnSelectionAllowed(false);
        JTableHeader header = getTableHeader();
        if (header != null) {
            MouseAdapter listMouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    TableColumnModel columnModel = getColumnModel();
                    int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                    int column = convertColumnIndexToModel(viewColumn);
                    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1 && !e.isAltDown() && column > 0) {
                        try {
                            validateColumn(column);
                            int shiftPressed = e.getModifiers() & InputEvent.SHIFT_MASK;
                            boolean ascending = (shiftPressed == 0);
                            getInstancesTableModel().sort(column, attributesTable.getAttributeType(column - 1),
                                    ascending);
                        } catch (Exception ex) {
                            LoggerUtils.error(log, ex);
                            JOptionPane.showMessageDialog(InstancesTable.this.getRootPane(), ex.getMessage(), null,
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            };
            header.addMouseListener(listMouseListener);
        }
    }

    private void validateValues() {
        for (int j = 1; j < getColumnCount(); j++) {
            if (attributesTable.isSelected(j - 1)) {
                validateColumn(j);
            }
        }
    }

    private void validateColumn(int j) {
        String attribute = getColumnName(j);
        int attrIndex = j - 1;
        for (int k = 0; k < getRowCount(); k++) {
            String str = (String) getValueAt(k, j);
            if (str != null) {
                try {
                    if (attributesTable.isNumeric(attrIndex)) {
                        if (!str.matches(DoubleDocument.DOUBLE_FORMAT)) {
                            throw new IllegalArgumentException(
                                    String.format(INCORRECT_NUMERIC_VALUES_ERROR_FORMAT, attribute));
                        }
                        isNumericOverflow(attribute, str);
                    }
                    if (attributesTable.isDate(attrIndex)) {
                        parseDate(attribute, str);
                    }
                } catch (Exception ex) {
                    changeSelection(k, j, false, false);
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
        }
    }

    private Instances createInstances(String relationName) throws ParseException {
        Instances newDataSet = new Instances(relationName, createAttributesList(), getRowCount());
        DecimalFormat format = getInstancesTableModel().format();
        for (int i = 0; i < getRowCount(); i++) {
            Instance obj = new DenseInstance(newDataSet.numAttributes());
            obj.setDataset(newDataSet);
            for (int j = 0; j < newDataSet.numAttributes(); j++) {
                Attribute attribute = newDataSet.attribute(j);
                String valueAt = (String) getValueAt(i, getAttrIndex(attribute.name()));
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
        lastDataModificationCount = getInstancesTableModel().getModificationCount();
        lastAttributesModificationCount = attributesTable.getAttributesTableModel().getModificationCount();
        lastClassModificationCount = classModificationCount;
        lastCreatedInstances = newInstances;
    }

    private boolean isDataModified() {
        return lastDataModificationCount != getInstancesTableModel().getModificationCount();
    }

    private boolean isAttributesModified() {
        return lastAttributesModificationCount != attributesTable.getAttributesTableModel().getModificationCount();
    }

    private boolean isClassModified() {
        return classModificationCount != lastClassModificationCount;
    }

    private boolean isInstancesModified() {
        return lastCreatedInstances == null || isDataModified() || isAttributesModified() || isClassModified();
    }

    private boolean validateSelectedAttributesCount() {
        int count = 0;
        for (int i = 0; i < attributesTable.getRowCount(); i++) {
            if (attributesTable.isSelected(i)) {
                count++;
            }
        }
        return count < MIN_NUMBER_OF_SELECTED_ATTRIBUTES;
    }

    private int getAttrIndex(String name) {
        return getTableHeader().getColumnModel().getColumnIndex(name);
    }

    private int getClassIndex() {
        return classBox.getSelectedIndex();
    }

    private ArrayList<Attribute> createAttributesList() {
        ArrayList<Attribute> attr = new ArrayList<>(getColumnCount() - 1);
        for (int i = 1; i < getColumnCount(); i++) {
            String attribute = getColumnName(i);
            int attrIndex = i - 1;
            if (attributesTable.isSelected(attrIndex)) {
                if (attributesTable.isNumeric(attrIndex)) {
                    attr.add(new Attribute(attribute));
                } else if (attributesTable.isDate(attrIndex)) {
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
        for (int j = 0; j < getRowCount(); j++) {
            String stringValue = (String) getValueAt(j, getAttrIndex(attribute));
            if (stringValue != null) {
                String trimValue = stringValue.trim();
                if (!StringUtils.isEmpty(trimValue) && !values.contains(trimValue)) {
                    values.add(stringValue.trim());
                }
            }
        }
        return new Attribute(attribute, values);
    }

    private List<Entry<String, Integer>> createAttributesInfo() {
        Instances data = data();
        List<Entry<String, Integer>> attributes = new ArrayList<>();
        for (int i = 0; i < data.numAttributes(); i++) {
            Entry<String, Integer> entry = new Entry<>();
            entry.setKey(data.attribute(i).name());
            if (attributesTable.isDate(i)) {
                entry.setValue(Attribute.DATE);
            } else if (attributesTable.isNumeric(i)) {
                entry.setValue(Attribute.NUMERIC);
            } else {
                entry.setValue(Attribute.NOMINAL);
            }
            attributes.add(entry);
        }
        return attributes;
    }
}
