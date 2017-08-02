/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.enums.AttributesTypes;
import eca.gui.tables.models.AttributesTableModel;
import eca.gui.text.DateFormat;
import eca.gui.text.DoubleDocument;
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
 *
 * @author Рома
 */

public class AttributesTable extends JDataTableBase {

    private final InstancesTable table;

    public AttributesTable(InstancesTable table, final JComboBox<String> classBox) {
        super(new AttributesTableModel(table.data()));
        this.table = table;
        //-------------------------------------------------------
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.getColumnModel().getColumn(0).setMaxWidth(50);
        this.getColumnModel().getColumn(0).setMinWidth(50);
        this.getColumnModel().getColumn(AttributesTableModel.EDIT_INDEX).setMaxWidth(20);
        JComboBox<String> types = new JComboBox<>();
        types.addItem(AttributesTypes.NOMINAL);
        types.addItem(AttributesTypes.NUMERIC);
        types.addItem(AttributesTypes.DATE);
        TableColumn col = this.getColumnModel().getColumn(AttributesTableModel.LIST_INDEX);
        col.setCellEditor(new JComboBoxEditor(types));
        col.setCellRenderer(new ComboBoxRenderer());
        //-------------------------------------------------
        JPopupMenu popMenu = this.getComponentPopupMenu();
        JMenuItem renameMenu = new JMenuItem("Переименовать атрибут");
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
                            "Имя:",
                            "Новое имя атрибута: " + table.data().attribute(i).name(),
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
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(AttributesTable.this.getRootPane(),
                                        "Атрибут с именем '" + trimName + "' уже существует!",
                                        null, JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
        //--------------------------------------------------
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
        return count < 2;
    }

    public final void check() throws Exception {
        if (table.getRowCount() == 0) {
            throw new Exception("Необходимо заполнить таблицу с данными!");
        }
        if (notSelected()) {
            throw new Exception("Выберите хотя бы 2 атрибута!");
        }
        if (isNumeric(data().classIndex())) {
            throw new Exception("Атрибут класса должен иметь категориальный тип!");
        }
        if (!isSelected(data().classIndex())) {
            throw new Exception("Не выбран атрибут класса!");
        }
        for (int j = 0; j < data().numAttributes(); j++) {
            Attribute a = data().attribute(j);
            if (isSelected(j)) {
                for (int k = 0; k < table.getRowCount(); k++) {
                    String str = (String) table.getValueAt(k, j + 1);
                    if (str != null) {
                        if (isNumeric(j) && !str.matches(DoubleDocument.DOUBLE_FORMAT)) {
                            throw new Exception("Недопустимые значения числового атрибута "
                                    + a.name() + "!");
                        }
                        if (isDate(j)) {
                            try {
                                DateFormat.SIMPLE_DATE_FORMAT.parse(str);
                            }
                            catch (Exception e) {
                                throw new Exception("Формат даты для атрибута  '"
                                        + a.name() + "'\nдолжен быть следующим: "
                                        + DateFormat.DATE_FORMAT);
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
                } else if (isDate(a.index()))  {
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
        //if (values.size() < 2) {
        //    throw new Exception("Категориальный атрибут '" + a.name()
        //            + "' должен\nиметь не менее двух значений!");
      //  }
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

        public ComboBoxRenderer() {
            this.addItem(AttributesTypes.NUMERIC);
            this.addItem(AttributesTypes.NOMINAL);
            this.addItem(AttributesTypes.DATE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                this.setForeground(table.getSelectionForeground());
                this.setBackground(table.getSelectionBackground());
            } else {
                this.setForeground(table.getForeground());
                this.setBackground(table.getBackground());
            }
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

        public JComboBoxEditor(JComboBox<String> box) {
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
