/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.service.ClassifierInputOptionsService;
import eca.gui.GuiUtils;
import eca.gui.tables.models.ClassifyInstanceTableModel;
import eca.gui.text.DoubleDocument;
import eca.text.NumericFormat;
import eca.statistics.AttributeStatistics;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.Enumeration;

/**
 * @author Roman Batygin
 */
public class ClassifyInstanceTable extends JDataTableBase {

    private static final int DOUBLE_FIELD_LENGTH = 12;
    private static final int MAX_FIELD_LENGTH = 255;
    private static final int ROW_HEIGHT = 18;
    private static final String ATTR_VALUE_NOT_SPECIFIED_ERROR_FORMAT = "Не задано значение атрибута '%s'";
    private static final String INCORRECT_ATTR_VALUE_ERROR_FORMAT = "Недопустимое значение атрибута '%s'";
    private final DecimalFormat decimalFormat = NumericFormat.getInstance();
    private AttributeStatistics attributeStatistics;

    public ClassifyInstanceTable(Instances data, int digits) {
        super(new ClassifyInstanceTableModel(data));
        decimalFormat.setMaximumFractionDigits(digits);
        attributeStatistics = new AttributeStatistics(data, decimalFormat);
        TableColumn column = this.getColumnModel().getColumn(ClassifyInstanceTableModel.TEXT_INDEX);
        this.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.getColumnModel().getColumn(0).setMaxWidth(50);
        this.getColumnModel().getColumn(0).setMinWidth(50);
        JTextField text = new JTextField(DOUBLE_FIELD_LENGTH);
        text.setDocument(new DoubleDocument(MAX_FIELD_LENGTH));
        column.setCellEditor(new DefaultCellEditor(text));
        this.getColumnModel().getColumn(1).setCellRenderer(new AttributeRenderer(data));
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

    public AttributeStatistics getAttributeStatistics() {
        return attributeStatistics;
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public Instances data() {
        return model().data();
    }

    public Instance instance() throws Exception {
        Object[] vector = model().values();
        Instances data = data();
        Enumeration<Attribute> en = data.enumerateAttributes();
        Instance ins = new DenseInstance(data.numAttributes());
        ins.setDataset(data);
        try {
            while (en.hasMoreElements()) {
                Attribute a = en.nextElement();
                String strValue = (String) vector[a.index()];
                if (strValue == null || strValue.isEmpty()) {
                    throw new Exception(String.format(ATTR_VALUE_NOT_SPECIFIED_ERROR_FORMAT, a.name()));
                }
                double value = decimalFormat.parse(strValue).doubleValue();
                if (a.isNominal() && (!strValue.matches("^[0-9]+$") || !a.isInRange(value))) {
                    throw new Exception(String.format(INCORRECT_ATTR_VALUE_ERROR_FORMAT, a.name()));
                }
                ins.setValue(a, value);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return ins;
    }

    public void reset() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(null, i, ClassifyInstanceTableModel.TEXT_INDEX);
        }
    }

    public ClassifyInstanceTableModel model() {
        return (ClassifyInstanceTableModel) this.getModel();
    }

    /**
     *
     */
    private class AttributeRenderer extends JTextField
            implements TableCellRenderer {

        Instances data;

        AttributeRenderer(Instances data) {
            this.data = data;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            int i = row >= data.classIndex() ? row + 1 : row;
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setToolTipText(ClassifierInputOptionsService.getAttributeInfoAsHtml(data.attribute(i),
                    attributeStatistics).toString());
            this.setText(value.toString());
            this.setBorder(null);
            this.setFont(ClassifyInstanceTable.this.getTableHeader().getFont());
            return this;
        }

    } // End of class AttributeRender

}
