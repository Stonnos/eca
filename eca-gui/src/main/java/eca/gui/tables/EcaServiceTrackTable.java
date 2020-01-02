/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.tables.models.EcaServiceTrackTableModel;
import eca.model.EcaServiceTrack;
import eca.model.TrackStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Map;

import static com.google.common.collect.Maps.newEnumMap;
import static eca.gui.tables.models.EcaServiceTrackTableModel.STATUS_COLUMN;

/**
 * @author Roman Batygin
 */
public class EcaServiceTrackTable extends JDataTableBase {

    private static final Map<TrackStatus, Color> trackStatusColorMap;

    static {
        trackStatusColorMap = newEnumMap(TrackStatus.class);
        trackStatusColorMap.put(TrackStatus.REQUEST_SENT, Color.BLUE);
        trackStatusColorMap.put(TrackStatus.RESPONSE_RECEIVED, Color.GREEN);
    }

    public EcaServiceTrackTable() {
        super(new EcaServiceTrackTableModel());
        this.getColumnModel().getColumn(STATUS_COLUMN).setCellRenderer(new TrackStatusCellRenderer());
        this.setAutoResizeOff(false);
    }

    public EcaServiceTrack getTrack(String correlationId) {
        return getEcaServiceTrackTableModel().getTrack(correlationId);
    }

    public void addTrack(EcaServiceTrack ecaServiceTrack) {
        getEcaServiceTrackTableModel().addTrack(ecaServiceTrack);
    }

    public void updateTrackStatus(String correlationId, TrackStatus status) {
        getEcaServiceTrackTableModel().updateTrackStatus(correlationId, status);
    }

    private EcaServiceTrackTableModel getEcaServiceTrackTableModel() {
        return (EcaServiceTrackTableModel) this.getModel();
    }

    private class TrackStatusCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == STATUS_COLUMN) {
                EcaServiceTrack ecaServiceTrack = getEcaServiceTrackTableModel().getTrack(row);
                Color color = trackStatusColorMap.getOrDefault(ecaServiceTrack.getStatus(), table.getForeground());
                cell.setForeground(color);
            } else {
                cell.setForeground(table.getForeground());
            }
            return cell;
        }
    }

}
