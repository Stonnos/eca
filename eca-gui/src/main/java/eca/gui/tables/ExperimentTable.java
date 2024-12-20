package eca.gui.tables;

import eca.config.ConfigurationService;
import eca.core.InstancesHandler;
import eca.core.evaluation.EvaluationResults;
import eca.dataminer.AbstractExperiment;
import eca.gui.GuiUtils;
import eca.gui.editors.JButtonEditor;
import eca.gui.frames.results.ClassificationResultsFrameBase;
import eca.gui.frames.results.ClassificationResultsFrameFactory;
import eca.gui.logging.LoggerUtils;
import eca.gui.renderers.JButtonRenderer;
import eca.gui.tables.models.EnsembleTableModel;
import eca.gui.tables.models.ExperimentTableModel;
import eca.model.ReferenceWrapper;
import eca.report.ReportGenerator;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newHashMap;
import static eca.util.ClassifierNamesFactory.getClassifierName;

/**
 * @author Roman Batygin
 */
@Slf4j
public class ExperimentTable extends JDataTableBase {

    private static final ConfigurationService CONFIG_SERVICE =
            ConfigurationService.getApplicationConfigService();
    private static final int INDEX_COLUMN_MAX_WIDTH = 50;
    private static final int CLASSIFIER_INDEX_COLUMN_MIN_WIDTH = 350;

    private final JFrame parentFrame;
    private final Map<Integer, ClassificationResultsFrameBase> classificationResultsFrameBases = newHashMap();

    public ExperimentTable(AbstractExperiment<?> experiment, JFrame parent, int digits) {
        super(new ExperimentTableModel(experiment, digits));
        this.parentFrame = parent;
        this.getColumnModel().getColumn(ExperimentTableModel.CLASSIFIER_INDEX).setCellRenderer(
                new ClassifierRenderer());
        this.getColumnModel().getColumn(ExperimentTableModel.RESULTS_INDEX)
                .setCellRenderer(new JButtonRenderer(EnsembleTableModel.RESULT_TITLE));
        this.getColumnModel().getColumn(ExperimentTableModel.RESULTS_INDEX).setCellEditor(
                new JButtonExperimentEditor());
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent evt) {
                Point p = new Point(evt.getX(), evt.getY());
                int i = ExperimentTable.this.rowAtPoint(p);
                int j = ExperimentTable.this.columnAtPoint(p);
                ExperimentTable.this.changeSelection(i, j, false, false);
            }
        });
        this.getColumnModel().getColumn(ExperimentTableModel.INDEX).setMaxWidth(INDEX_COLUMN_MAX_WIDTH);
        this.getColumnModel().getColumn(ExperimentTableModel.CLASSIFIER_INDEX)
                .setMinWidth(CLASSIFIER_INDEX_COLUMN_MIN_WIDTH);
        this.setAutoResizeOff(false);
    }

    public ExperimentTableModel experimentModel() {
        return (ExperimentTableModel) this.getModel();
    }

    public void notifyLastInsertedResults() {
        experimentModel().notifyLastInsertedResults();
    }

    public void sortByBestResults() {
        experimentModel().getExperiment().sortByBestResults();
        notifyDataChanged();
    }

    public void notifyDataChanged() {
        experimentModel().fireTableDataChanged();
    }

    public void initializeExperimentHistory(AbstractExperiment<?> experiment) {
        clear();
        experimentModel().setExperiment(experiment);
    }

    public void clear() {
        boolean isEmpty = experimentModel().getExperiment().getHistory().isEmpty();
        experimentModel().clear();
        classificationResultsFrameBases.forEach((key, value) -> value.dispose());
        classificationResultsFrameBases.clear();
        if (!isEmpty) {
            SwingUtilities.invokeLater(System::gc);
        }
    }

    public void clearAndResetExperimentModel() {
        clear();
        experimentModel().resetExperimentModel();
    }

    public void setRenderer(final Color color) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected,
                        hasFocus, row, column);
                if (row < CONFIG_SERVICE.getApplicationConfig().getExperimentConfig().getNumBestResults()) {
                    cell.setForeground(color);
                } else {
                    cell.setForeground(table.getForeground());
                }
                return cell;
            }
        };
        this.getColumnModel().getColumn(ExperimentTableModel.ACCURACY_INDEX).setCellRenderer(renderer);
    }

    /**
     * Classifier renderer.
     */
    private class ClassifierRenderer extends JTextField implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            this.setToolTipText(ReportGenerator.getClassifierInputOptionsAsHtml(experimentModel()
                    .getClassifier(row), false));
            this.setText(Optional.ofNullable(value).map(Object::toString).orElse(null));
            this.setBorder(null);
            this.setFont(ExperimentTable.this.getTableHeader().getFont());
            return this;
        }
    }

    /**
     * Experiment results editor.
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
                if (!classificationResultsFrameBases.containsKey(index)) {
                    ExperimentTableModel model = experimentModel();
                    Instances dataSet = ((InstancesHandler) classifierDescriptor.getClassifier()).getData();
                    String title = getClassifierName(classifierDescriptor.getClassifier());
                    ClassificationResultsFrameBase result =
                            ClassificationResultsFrameFactory.buildClassificationResultsFrameBase(parentFrame, title,
                                    new ReferenceWrapper<>(classifierDescriptor.getClassifier()), dataSet,
                                    classifierDescriptor.getEvaluation(), model.digits());
                    classificationResultsFrameBases.put(index, result);
                }
                classificationResultsFrameBases.get(index).setVisible(true);
                this.classifierDescriptor = null;
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(ExperimentTable.this.getParent(), e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
