/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.evaluation.EvaluationResults;
import eca.gui.GuiUtils;
import eca.gui.JButtonEditor;
import eca.gui.JButtonRenderer;
import eca.gui.frames.ClassificationResultsFrameBase;
import eca.gui.logging.LoggerUtils;
import eca.gui.service.ClassifierInputOptionsService;
import eca.gui.tables.models.EnsembleTableModel;
import eca.gui.tables.models.ExperimentTableModel;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ExperimentTable extends JDataTableBase {

    private final JFrame parent;
    private final Instances data;
    private final ArrayList<ClassificationResultsFrameBase> classificationResultsFrameBases = new ArrayList<>();


    public ExperimentTable(ArrayList<EvaluationResults> experiment,
                           JFrame parent, Instances data, int digits) throws Exception {
        super(new ExperimentTableModel(experiment, digits));
        this.parent = parent;
        this.data = data;
        this.getColumnModel().getColumn(1).setCellRenderer(new ClassifierRenderer());
        this.getColumnModel().getColumn(3)
                .setCellRenderer(new JButtonRenderer(EnsembleTableModel.RESULT_TITLE));
        this.getColumnModel().getColumn(3).setCellEditor(new JButtonExperimentEditor());
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

    public void addExperiment(EvaluationResults val) {
        experimentModel().add(val);
        classificationResultsFrameBases.add(null);
    }

    public void setExperiment(List<EvaluationResults> evaluationResults) {
        clear();
        for (EvaluationResults results : evaluationResults) {
            addExperiment(results);
        }
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
            this.setToolTipText(ClassifierInputOptionsService.getInputOptionsInfoAsHtml(experimentModel().getClassifier(row)));
            this.setText(value.toString());
            this.setBorder(null);
            this.setFont(ExperimentTable.this.getTableHeader().getFont());
            return this;
        }
    } //End of class ClassifierRenderer

    /**
     *
     */
    private class JButtonExperimentEditor extends JButtonEditor {

        EvaluationResults classifierDescriptor;
        int index;

        JButtonExperimentEditor() {
            super(ExperimentTableModel.RESULT_TITLE);
        }

        @Override
        protected void doOnPushing(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            this.index = row;
            this.classifierDescriptor = experimentModel().get(row);
        }

        @Override
        protected void doAfterPushing() {
            try {
                if (classificationResultsFrameBases.get(index) == null) {
                    ExperimentTableModel model = experimentModel();
                    ClassificationResultsFrameBase result =
                            new ClassificationResultsFrameBase(parent, classifierDescriptor.getClassifier().getClass()
                                    .getSimpleName(), classifierDescriptor.getClassifier(), data,
                                    classifierDescriptor.getEvaluation(), model.digits());
                    ClassificationResultsFrameBase.createResults(result, model.digits());
                    StatisticsTableBuilder stat = new StatisticsTableBuilder(model.digits());
                    result.setStatisticsTable(stat.createStatistics(classifierDescriptor.getClassifier(),
                            classifierDescriptor.getEvaluation()));
                    classificationResultsFrameBases.set(index, result);
                }
                classificationResultsFrameBases.get(index).setVisible(true);
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(ExperimentTable.this.getParent(), e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
