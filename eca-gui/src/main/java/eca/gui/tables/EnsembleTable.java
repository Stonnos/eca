/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.core.InstancesHandler;
import eca.core.evaluation.Evaluation;
import eca.gui.Cleanable;
import eca.gui.GuiUtils;
import eca.gui.editors.JButtonEditor;
import eca.gui.frames.results.ClassificationResultsFrameBase;
import eca.gui.frames.results.ClassificationResultsFrameFactory;
import eca.gui.logging.LoggerUtils;
import eca.gui.renderers.JButtonRenderer;
import eca.gui.tables.models.EnsembleTableModel;
import eca.model.ReferenceWrapper;
import eca.report.ReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import weka.classifiers.Classifier;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static eca.util.ClassifierNamesFactory.getClassifierName;

/**
 * Implements ensemble table.
 *
 * @author Roman Batygin
 */
@Slf4j
public class EnsembleTable extends JDataTableBase implements Cleanable {

    private static final int INDEX_COLUMN_MAX_WIDTH = 50;
    private final JFrame parentFrame;
    private final int digits;
    private ClassificationResultsFrameBase[] classificationResultsFrameBases;

    public EnsembleTable(List<Classifier> classifierArrayList, JFrame parent, int digits) {
        super(new EnsembleTableModel(classifierArrayList));
        this.parentFrame = parent;
        this.digits = digits;
        this.classificationResultsFrameBases = new ClassificationResultsFrameBase[classifierArrayList.size()];
        this.getColumnModel().getColumn(EnsembleTableModel.CLASSIFIER_INDEX).setCellRenderer(new ClassifierRenderer());
        this.getColumnModel().getColumn(EnsembleTableModel.RESULTS_INDEX)
                .setCellRenderer(new JButtonRenderer(EnsembleTableModel.RESULT_TITLE));
        this.getColumnModel().getColumn(EnsembleTableModel.RESULTS_INDEX).setCellEditor(new JButtonEnsembleEditor());
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = EnsembleTable.this.rowAtPoint(p);
                int j = EnsembleTable.this.columnAtPoint(p);
                EnsembleTable.this.changeSelection(i, j, false, false);
            }
        });
        this.addHideClassificationResultsFramesListener();
        this.getColumnModel().getColumn(EnsembleTableModel.INDEX).setMaxWidth(INDEX_COLUMN_MAX_WIDTH);
        this.setAutoResizeOff(false);
    }

    public EnsembleTableModel ensembleModel() {
        return (EnsembleTableModel) this.getModel();
    }

    private void addHideClassificationResultsFramesListener() {
        this.parentFrame.addWindowListener(new HideClassificationResultsFramesListener(classificationResultsFrameBases));
    }

    @Override
    public void clear() {
        ensembleModel().clear();
        IntStream.range(0, classificationResultsFrameBases.length).forEach(i -> {
            if (classificationResultsFrameBases[i] != null) {
                classificationResultsFrameBases[i].setVisible(false);
                classificationResultsFrameBases[i].dispose();
                classificationResultsFrameBases[i] = null;
            }
        });
    }

    @RequiredArgsConstructor
    private static class HideClassificationResultsFramesListener extends WindowAdapter {

        private final ClassificationResultsFrameBase[] resultsFrameBases;

        @Override
        public void windowClosing(WindowEvent e) {
            IntStream.range(0, resultsFrameBases.length).forEach(i -> {
                if (resultsFrameBases[i] != null) {
                    resultsFrameBases[i].setVisible(false);
                }
            });
        }
    }

    /**
     * Classifier renderer.
     */
    private class ClassifierRenderer extends JTextField
            implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setToolTipText(
                    ReportGenerator.getClassifierInputOptionsAsHtml(ensembleModel().get(row), false));
            this.setText(Optional.ofNullable(value).map(Object::toString).orElse(null));
            this.setBorder(null);
            this.setFont(EnsembleTable.this.getTableHeader().getFont());
            return this;
        }
    }

    /**
     * Ensemble results editor.
     */
    private class JButtonEnsembleEditor extends JButtonEditor {

        Classifier classifier;
        int index;

        JButtonEnsembleEditor() {
            super(EnsembleTableModel.RESULT_TITLE);
        }

        @Override
        protected void doOnPushing(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            this.index = row;
            this.classifier = ensembleModel().get(row);
        }

        @Override
        protected void doAfterPushing() {
            try {
                if (classificationResultsFrameBases[index] == null) {
                    Instances data = ((InstancesHandler) classifier).getData();
                    Evaluation evaluation = new Evaluation(data);
                    evaluation.evaluateModel(classifier, data);
                    String title = getClassifierName(classifier);
                    ClassificationResultsFrameBase result =
                            ClassificationResultsFrameFactory.buildClassificationResultsFrameBase(parentFrame, title,
                                    new ReferenceWrapper<>(classifier), data, evaluation, digits);
                    classificationResultsFrameBases[index] = result;
                }
                classificationResultsFrameBases[index].setVisible(true);
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(EnsembleTable.this.getParent(), e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
