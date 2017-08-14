/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.InstancesHandler;
import eca.core.evaluation.Evaluation;
import eca.ensemble.EnsembleClassifier;
import eca.gui.ClassifierInputInfo;
import eca.gui.frames.ResultsFrameBase;
import eca.gui.tables.models.EnsembleTableModel;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author Рома
 */
public class EnsembleTable extends JDataTableBase {

    private final JFrame parent;
    private final int digits;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    public EnsembleTable(EnsembleClassifier struct, JFrame parent, int digits) throws Exception {
        super(new EnsembleTableModel(struct));
        this.parent = parent;
        this.digits = digits;
        this.getColumnModel().getColumn(1).setCellRenderer(new ClassifierRenderer());
        this.getColumnModel().getColumn(2).setCellRenderer(new JButtonRenderer());
        this.getColumnModel().getColumn(2).setCellEditor(new JButtonEditor(new JCheckBox()));
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = EnsembleTable.this.rowAtPoint(p);
                int j = EnsembleTable.this.columnAtPoint(p);
                EnsembleTable.this.changeSelection(i, j, false, false);
            }
        });
        this.getColumnModel().getColumn(0).setMaxWidth(50);
        this.setAutoResizeOff(false);
    }

    public EnsembleTableModel ensembleModel() {
        return (EnsembleTableModel) this.getModel();
    }

    /**
     *
     */
    private class ClassifierRenderer extends JTextField
            implements TableCellRenderer {

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
            this.setToolTipText(ClassifierInputInfo.getInfo(ensembleModel().get(row)));
            this.setText(value.toString());
            this.setBorder(null);
            this.setFont(EnsembleTable.this.getTableHeader().getFont());
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
            if (isSelected) {
                this.setForeground(table.getSelectionForeground());
                this.setBackground(table.getSelectionBackground());
            } else {
                this.setForeground(table.getForeground());
                this.setBackground(table.getBackground());
            }
            this.setFont(new Font(EnsembleTable.this.getFont().getName(), Font.BOLD,
                    EnsembleTable.this.getFont().getSize()));
            this.setText(EnsembleTableModel.RESULT_TITLE);
            return this;
        }

    } // End of class JButtonRender

    private class JButtonEditor extends DefaultCellEditor {

        private JButton button;
        private boolean isPushed;
        private ResultsFrameBase result;
        private Classifier classifier;

        public JButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            this.setClickCountToStart(0);
            button = new JButton();
            button.setOpaque(true);
            button.setCursor(handCursor);
            button.setText(EnsembleTableModel.RESULT_TITLE);
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
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(table.getBackground());
            }
            button.setFont(new Font(EnsembleTable.this.getFont().getName(), Font.BOLD,
                    EnsembleTable.this.getFont().getSize()));
            isPushed = true;
            classifier = ensembleModel().get(row);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    Instances data = ((InstancesHandler) classifier).getData();
                    Evaluation e = new Evaluation(data);
                    e.evaluateModel(classifier, data);
                    result = new ResultsFrameBase(parent, classifier.getClass().getSimpleName(),
                            classifier, data, e, digits);
                    ResultsFrameBase.createResults(result, digits);
                    StatisticsTableBuilder stat = new StatisticsTableBuilder(digits);
                    result.setStatisticaTable(stat.createStatistica(classifier, e));
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(EnsembleTable.this.getParent(), e.getMessage(),
                            null, JOptionPane.ERROR_MESSAGE);
                }
                result.setVisible(true);
            }
            isPushed = false;
            return EnsembleTableModel.RESULT_TITLE;
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
