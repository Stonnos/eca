package eca.gui.tables;

import eca.gui.Cleanable;
import eca.gui.editors.JButtonEditor;
import eca.gui.renderers.JButtonRenderer;
import eca.gui.frames.InstancesFrame;
import eca.gui.logging.LoggerUtils;
import eca.gui.tables.models.InstancesSetTableModel;
import lombok.extern.slf4j.Slf4j;
import weka.core.Instances;

import javax.swing.*;
import java.util.ArrayList;

/**
 * @author Roman Batygin
 */
@Slf4j
public class InstancesSetTable extends JDataTableBase implements Cleanable {

    private static final int BUTTON_WIDTH = 125;

    private final JFrame parentFrame;
    private final ArrayList<InstancesFrame> instancesFrameArrayList = new ArrayList<>();

    public InstancesSetTable(JFrame parentFrame) {
        super(new InstancesSetTableModel());
        this.parentFrame = parentFrame;
        this.getColumnModel().getColumn(InstancesSetTableModel.BUTTON_INDEX)
                .setCellRenderer(new JButtonRenderer(InstancesSetTableModel.RESULT_TITLE));
        this.getColumnModel().getColumn(InstancesSetTableModel.BUTTON_INDEX)
                .setCellEditor(new JButtonInstancesEditor());
        this.getColumnModel().getColumn(InstancesSetTableModel.BUTTON_INDEX).setMinWidth(BUTTON_WIDTH);
        this.setAutoResizeOff(false);
    }

    public InstancesSetTableModel getInstancesSetTableModel() {
        return (InstancesSetTableModel) getModel();
    }

    public void addInstances(Instances data) {
        getInstancesSetTableModel().add(data);
        instancesFrameArrayList.add(null);
    }

    @Override
    public void clear() {
        instancesFrameArrayList.forEach(instancesFrame -> {
            if (instancesFrame != null) {
                instancesFrame.dispose();
            }
        });
        instancesFrameArrayList.clear();
        getInstancesSetTableModel().clear();
    }

    /**
     *
     */
    private class JButtonInstancesEditor extends JButtonEditor {

        Instances data;
        int index;

        JButtonInstancesEditor() {
            super(InstancesSetTableModel.RESULT_TITLE);
        }

        @Override
        protected void doOnPushing(JTable table, Object value,
                                   boolean isSelected, int row, int column) {
            this.index = row;
            this.data = getInstancesSetTableModel().getInstances(row);
        }

        @Override
        protected void doAfterPushing() {
            try {
                if (instancesFrameArrayList.get(index) == null) {
                    instancesFrameArrayList.set(index, new InstancesFrame(data, parentFrame));
                }
                instancesFrameArrayList.get(index).setVisible(true);
                data = null;
            } catch (Exception e) {
                LoggerUtils.error(log, e);
                JOptionPane.showMessageDialog(parentFrame, e.getMessage(),
                        null, JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
