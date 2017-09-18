package eca.gui.tables;

import eca.gui.JButtonEditor;
import eca.gui.JButtonRenderer;
import eca.gui.frames.InstancesFrame;
import eca.gui.frames.QueryFrame;
import eca.gui.frames.ResultsFrameBase;
import eca.gui.tables.models.EnsembleTableModel;
import eca.gui.tables.models.ExperimentTableModel;
import eca.gui.tables.models.InstancesSetTableModel;
import eca.model.ClassifierDescriptor;
import weka.core.Instances;

import javax.swing.*;

/**
 * @author Roman Batygin
 */

public class InstancesSetTable extends JDataTableBase {

    private JFrame parent;

    public InstancesSetTable(JFrame parent) {
        super(new InstancesSetTableModel());
        this.parent = parent;
        this.getColumnModel().getColumn(InstancesSetTableModel.BUTTON_INDEX)
                .setCellRenderer(new JButtonRenderer(InstancesSetTableModel.RESULT_TITLE));
        this.getColumnModel().getColumn(InstancesSetTableModel.BUTTON_INDEX)
                .setCellEditor(new JButtonInstancesEditor());
        this.setAutoResizeOff(false);
    }

    public InstancesSetTableModel getInstancesSetTableModel() {
        return (InstancesSetTableModel) getModel();
    }

    public void addInstances(Instances data) {
        getInstancesSetTableModel().add(data);
    }

    /**
     *
     */
    private class JButtonInstancesEditor extends JButtonEditor {

        private Instances data;

        public JButtonInstancesEditor() {
            super(InstancesSetTableModel.RESULT_TITLE);
        }

        @Override
        protected void doOnPushing(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            data = getInstancesSetTableModel().getInstances(row);
        }

        @Override
        protected void doAfterPushing() {
            try {
                InstancesFrame instancesFrame = new InstancesFrame(data, parent);
                instancesFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
