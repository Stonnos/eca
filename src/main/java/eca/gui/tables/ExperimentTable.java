/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.ClassifierInputInfo;
import eca.gui.GuiUtils;
import eca.gui.frames.ResultsFrameBase;
import eca.gui.tables.models.ExperimentTableModel;
import eca.model.ClassifierDescriptor;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
public class ExperimentTable extends JDataTableBase {

    private final JFrame parent;
    private final Instances data;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    public ExperimentTable(ArrayList<ClassifierDescriptor> experiment,
                           JFrame parent, Instances data, int digits) throws Exception {
        super(new ExperimentTableModel(experiment, digits));
        this.parent = parent;
        this.data = data;
        this.getColumnModel().getColumn(1).setCellRenderer(new ClassifierRenderer());
        this.getColumnModel().getColumn(3).setCellRenderer(new JButtonRenderer());
        this.getColumnModel().getColumn(3).setCellEditor(new JButtonEditor(new JCheckBox()));
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = ExperimentTable.this.rowAtPoint(p);
                int j = ExperimentTable.this.columnAtPoint(p);
                ExperimentTable.this.changeSelection(i, j, false, false);
            }
        });
        this.getColumnModel().getColumn(0).setMaxWidth(50);
        this.setAutoResizeOff(false);
    }

    public ExperimentTableModel experimentModel() {
        return (ExperimentTableModel) this.getModel();
    }

    public void addExperiment(ClassifierDescriptor val) {
        experimentModel().add(val);
    }

    public void clear() {
        experimentModel().clear();
    }

    public int getBestNumber() {
        return 10;
    }

    public void setRenderer(final Color color) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (row < getBestNumber()) {
                    cell.setForeground(color);
                } else {
                    cell.setForeground(table.getForeground());
                }
                return cell;
            }
        };
        this.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    public void sort() {
        experimentModel().sort();
    }

    /**
     *
     */
    private class ClassifierRenderer extends JTextField
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setToolTipText(ClassifierInputInfo.getInputOptionsInfo(experimentModel().getClassifier(row)));
            this.setText(value.toString());
            this.setBorder(null);
            this.setFont(ExperimentTable.this.getTableHeader().getFont());
            return this;
        }
    } //End of class ClassifierRenderer

    /**
     *
     */
    private class JButtonRenderer extends JButton
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setFont(new Font(ExperimentTable.this.getFont().getName(), Font.BOLD,
                    ExperimentTable.this.getFont().getSize()));
            this.setText(ExperimentTableModel.RESULT_TITLE);
            return this;
        }

    } // End of class JButtonRender

    private class JButtonEditor extends DefaultCellEditor {

        private JButton button;
        private boolean isPushed;
        private ResultsFrameBase result;
        private ClassifierDescriptor object;

        public JButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            this.setClickCountToStart(0);
            button = new JButton();
            button.setOpaque(true);
            button.setCursor(handCursor);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(button, table, isSelected);
            button.setText(ExperimentTableModel.RESULT_TITLE);
            button.setFont(new Font(ExperimentTable.this.getFont().getName(), Font.BOLD,
                    ExperimentTable.this.getFont().getSize()));
            isPushed = true;
            object = experimentModel().get(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    ExperimentTableModel model = experimentModel();
                    result = new ResultsFrameBase(parent, object.getClassifier().getClass().getSimpleName(),
                            object.getClassifier(), data, object.getEvaluation(), model.digits());
                    ResultsFrameBase.createResults(result, model.digits());
                    StatisticsTableBuilder stat = new StatisticsTableBuilder(model.digits());
                    result.setStatisticaTable(stat.createStatistics(object.getClassifier(), object.getEvaluation()));
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ExperimentTable.this.getParent(), e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
                result.setVisible(true);
            }
            isPushed = false;
            return ExperimentTableModel.RESULT_TITLE;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

}
