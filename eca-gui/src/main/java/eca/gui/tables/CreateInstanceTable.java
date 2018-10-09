/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.tables.models.CreateInstanceTableModel;
import eca.gui.text.DoubleDocument;
import eca.gui.text.LengthDocument;
import eca.util.Entry;
import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import static eca.gui.service.ValidationService.isNumericOverflow;
import static eca.gui.service.ValidationService.parseDate;

/**
 * New instance creation table.
 *
 * @author Roman Batygin
 */
public class CreateInstanceTable extends JDataTableBase {

    private static final int FIELD_LENGTH = 20;
    private static final int MAX_FIELD_LENGTH = 255;
    private static final int ROW_HEIGHT = 18;

    private static final int NUMERATOR_COLUMN_WIDTH = 50;
    private static final int INFO_COLUMN_WIDTH = 240;

    public CreateInstanceTable(List<Entry<String, Integer>> attributes) {
        super(new CreateInstanceTableModel(attributes));
        TableColumn column = this.getColumnModel().getColumn(CreateInstanceTableModel.INPUT_TEXT_COLUMN_INDEX);
        this.getColumnModel().getColumn(CreateInstanceTableModel.NUMERATOR_COLUMN_INDEX).setPreferredWidth(
                NUMERATOR_COLUMN_WIDTH);
        this.getColumnModel().getColumn(CreateInstanceTableModel.NUMERATOR_COLUMN_INDEX).setMaxWidth(
                NUMERATOR_COLUMN_WIDTH);
        this.getColumnModel().getColumn(CreateInstanceTableModel.NUMERATOR_COLUMN_INDEX).setMinWidth(
                NUMERATOR_COLUMN_WIDTH);

        this.getColumnModel().getColumn(CreateInstanceTableModel.ATTRIBUTE_INFO_COLUMN_INDEX).setPreferredWidth(
                INFO_COLUMN_WIDTH);
        this.getColumnModel().getColumn(CreateInstanceTableModel.ATTRIBUTE_INFO_COLUMN_INDEX).setMaxWidth(
                INFO_COLUMN_WIDTH);
        this.getColumnModel().getColumn(CreateInstanceTableModel.ATTRIBUTE_INFO_COLUMN_INDEX).setMinWidth(
                INFO_COLUMN_WIDTH);

        JTextField textField = new JTextField(FIELD_LENGTH);
        column.setCellEditor(new AttributeValueFieldEditor(textField));

        this.setRowHeight(ROW_HEIGHT);
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = rowAtPoint(p);
                int j = columnAtPoint(p);
                changeSelection(i, j, false, false);
            }
        });
        this.setAutoResizeOff(false);
    }

    public List<Object> getValues() {
        List<Object> valuesList = new ArrayList<>();
        String[] values = ((CreateInstanceTableModel) getModel()).getValues();
        List<Entry<String, Integer>> attributes = ((CreateInstanceTableModel) getModel()).getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            String value = values[i];
            if (!StringUtils.isEmpty(value)) {
                String trimValue = value.trim();
                valuesList.add(!trimValue.isEmpty() ? trimValue : null);
            } else {
                valuesList.add(null);
            }
        }
        return valuesList;
    }

    public void validateValues() {
        String[] values = ((CreateInstanceTableModel) getModel()).getValues();
        List<Entry<String, Integer>> attributes = ((CreateInstanceTableModel) getModel()).getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            String value = values[i];
            String attrName = attributes.get(i).getKey();
            if (!StringUtils.isEmpty(value)) {
                try {
                    if (attributes.get(i).getValue() == Attribute.DATE) {
                        parseDate(attrName, value);
                    } else if (attributes.get(i).getValue() == Attribute.NUMERIC) {
                        isNumericOverflow(attrName, value);
                    }
                } catch (Exception ex) {
                    changeSelection(i, CreateInstanceTableModel.INPUT_TEXT_COLUMN_INDEX, false, false);
                    throw new IllegalArgumentException(ex.getMessage());
                }
            }
        }
    }

    /**
     * Attribute value field editor.
     */
    private class AttributeValueFieldEditor extends DefaultCellEditor {

        AttributeValueFieldEditor(JTextField textField) {
            super(textField);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column) {
            Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            List<Entry<String, Integer>> attributes = ((CreateInstanceTableModel) getModel()).getAttributes();
            JTextField textField = (JTextField) component;
            if (attributes.get(row).getValue() == Attribute.NUMERIC) {
                textField.setDocument(new DoubleDocument(MAX_FIELD_LENGTH));
            } else {
                textField.setDocument(new LengthDocument(MAX_FIELD_LENGTH));
            }
            return component;
        }
    }

}
