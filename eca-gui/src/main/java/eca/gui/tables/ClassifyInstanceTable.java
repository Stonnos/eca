/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.config.ConfigurationService;
import eca.gui.GuiUtils;
import eca.gui.service.ClassifierInputOptionsService;
import eca.gui.tables.models.ClassifyInstanceTableModel;
import eca.gui.text.DoubleDocument;
import eca.gui.text.IntegerDocument;
import eca.gui.text.LengthDocument;
import eca.statistics.AttributeStatistics;
import eca.text.NumericFormatFactory;
import org.apache.commons.lang3.StringUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Instances classification panel.
 *
 * @author Roman Batygin
 */
public class ClassifyInstanceTable extends JDataTableBase {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
            new SimpleDateFormat(CONFIG_SERVICE.getApplicationConfig().getDateFormat());

    private static final int DOUBLE_FIELD_LENGTH = 12;
    private static final int INT_FIELD_LENGTH = 8;
    private static final int MAX_FIELD_LENGTH = 255;
    private static final int ROW_HEIGHT = 18;

    private static final int NUMERATOR_COLUMN_WIDTH = 50;
    private static final int ADDITIONAL_INFO_COLUMN_WIDTH = 200;

    private static final String ATTR_VALUE_NOT_SPECIFIED_ERROR_FORMAT = "Не задано значение атрибута '%s'";
    private static final String INVALID_ATTR_VALUE_ERROR_FORMAT = "Недопустимое значение атрибута '%s'";
    private static final String INVALID_DATE_FORMAT_ERROR = "Недопустимый формат даты для атрибута '%s'";
    private static final String INTEGER_REGEX = "^[0-9]+$";

    private final DecimalFormat decimalFormat = NumericFormatFactory.getInstance();
    private final AttributeStatistics attributeStatistics;

    public ClassifyInstanceTable(Instances data, int digits) {
        super(new ClassifyInstanceTableModel(data));
        decimalFormat.setMaximumFractionDigits(digits);
        attributeStatistics = new AttributeStatistics(data, decimalFormat);

        TableColumn column = this.getColumnModel().getColumn(ClassifyInstanceTableModel.INPUT_TEXT_COLUMN_INDEX);
        this.getColumnModel().getColumn(ClassifyInstanceTableModel.NUMERATOR_COLUMN_INDEX)
                .setPreferredWidth(NUMERATOR_COLUMN_WIDTH);
        this.getColumnModel().getColumn(ClassifyInstanceTableModel.NUMERATOR_COLUMN_INDEX)
                .setMaxWidth(NUMERATOR_COLUMN_WIDTH);
        this.getColumnModel().getColumn(ClassifyInstanceTableModel.NUMERATOR_COLUMN_INDEX)
                .setMinWidth(NUMERATOR_COLUMN_WIDTH);
        this.getColumnModel().getColumn(ClassifyInstanceTableModel.ADDITIONAL_INFO_COLUMN_INDEX)
                .setPreferredWidth(ADDITIONAL_INFO_COLUMN_WIDTH);
        this.getColumnModel().getColumn(ClassifyInstanceTableModel.ADDITIONAL_INFO_COLUMN_INDEX)
                .setMinWidth(ADDITIONAL_INFO_COLUMN_WIDTH);

        JTextField textField = new JTextField(DOUBLE_FIELD_LENGTH);
        column.setCellEditor(new AttributeTextFieldEditor(textField));

        this.getColumnModel().getColumn(ClassifyInstanceTableModel.ATTRIBUTE_INFO_COLUMN_INDEX)
                .setCellRenderer(new AttributeStatisticsRenderer());
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
        return getClassifyInstanceTableModel().data();
    }

    public Instance instance() throws ParseException {
        Object[] vector = getClassifyInstanceTableModel().values();
        Instances data = data();
        Enumeration<Attribute> en = data.enumerateAttributes();
        Instance instance = new DenseInstance(data.numAttributes());
        instance.setDataset(data);
        while (en.hasMoreElements()) {
            Attribute a = en.nextElement();
            String strValue = (String) vector[a.index()];
            if (StringUtils.isEmpty(strValue)) {
                throw new IllegalArgumentException(String.format(ATTR_VALUE_NOT_SPECIFIED_ERROR_FORMAT, a.name()));
            }
            if (a.isDate()) {
                try {
                    Date date = SIMPLE_DATE_FORMAT.parse(strValue);
                    instance.setValue(a, date.getTime());
                } catch (ParseException e) {
                    throw new IllegalArgumentException(String.format(INVALID_DATE_FORMAT_ERROR, a.name()));
                }
            } else {
                double value = decimalFormat.parse(strValue).doubleValue();
                if (a.isNominal() && (!strValue.matches(INTEGER_REGEX) || !a.isInRange(value))) {
                    throw new IllegalArgumentException(String.format(INVALID_ATTR_VALUE_ERROR_FORMAT, a.name()));
                }
                instance.setValue(a, value);
            }
        }
        return instance;
    }

    public void reset() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.setValueAt(null, i, ClassifyInstanceTableModel.INPUT_TEXT_COLUMN_INDEX);
        }
    }

    public ClassifyInstanceTableModel getClassifyInstanceTableModel() {
        return (ClassifyInstanceTableModel) this.getModel();
    }

    /**
     * Attribute statistics renderer.
     */
    private class AttributeStatisticsRenderer extends JTextField
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Instances data = getClassifyInstanceTableModel().data();
            int i = row >= data.classIndex() ? row + 1 : row;
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setToolTipText(ClassifierInputOptionsService.getAttributeStatisticsAsHtml(data.attribute(i),
                    attributeStatistics));
            this.setText(value.toString());
            this.setBorder(null);
            this.setFont(ClassifyInstanceTable.this.getTableHeader().getFont());
            return this;
        }

    }

    /**
     * Attribute text field editor.
     */
    private class AttributeTextFieldEditor extends DefaultCellEditor {

        AttributeTextFieldEditor(JTextField textField) {
            super(textField);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected,
                                                     int row, int column) {
            Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            Instances data = getClassifyInstanceTableModel().data();
            int i = row >= data.classIndex() ? row + 1 : row;
            JTextField textField = (JTextField) component;

            if (data.attribute(i).isDate()) {
                textField.setDocument(new LengthDocument(MAX_FIELD_LENGTH));
            } else if (data.attribute(i).isNumeric()) {
                textField.setDocument(new DoubleDocument(MAX_FIELD_LENGTH));
            } else {
                textField.setDocument(new IntegerDocument(INT_FIELD_LENGTH));
            }

            return component;
        }
    }

}
