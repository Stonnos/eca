/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.gui.tables;

import eca.gui.GuiUtils;
import eca.gui.tables.models.EcaServiceTrackTableModel;
import eca.model.EcaServiceTrack;
import eca.model.EcaServiceTrackStatus;
import eca.report.ReportGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.newEnumMap;
import static eca.gui.tables.models.EcaServiceTrackTableModel.DETAILS_COLUMN;
import static eca.gui.tables.models.EcaServiceTrackTableModel.RELATION_NAME_COLUMN;
import static eca.gui.tables.models.EcaServiceTrackTableModel.REQUEST_TYPE_COLUMN;
import static eca.gui.tables.models.EcaServiceTrackTableModel.STATUS_COLUMN;

/**
 * @author Roman Batygin
 */
public class EcaServiceTrackTable extends JDataTableBase {

    private static final int STATUS_COLUMN_WIDTH = 125;
    private static final int RELATION_NAME_COLUMN_WIDTH = 200;
    private static final int DETAILS_COLUMN_WIDTH = 350;
    private static final int REQUEST_TYPE_COLUMN_WIDTH = 300;

    private static final Map<EcaServiceTrackStatus, Color> trackStatusColorMap;

    static {
        trackStatusColorMap = newEnumMap(EcaServiceTrackStatus.class);
        trackStatusColorMap.put(EcaServiceTrackStatus.READY, Color.GRAY);
        trackStatusColorMap.put(EcaServiceTrackStatus.REQUEST_SENT, Color.BLUE);
        trackStatusColorMap.put(EcaServiceTrackStatus.RESPONSE_RECEIVED, Color.GREEN);
        trackStatusColorMap.put(EcaServiceTrackStatus.ERROR, Color.RED);
    }

    public EcaServiceTrackTable() {
        super(new EcaServiceTrackTableModel());
        this.initColumns();
        this.setAutoResizeOff(false);
        this.setCellSelectionEnabled(false);
    }

    public EcaServiceTrack getTrack(String correlationId) {
        return getEcaServiceTrackTableModel().getTrack(correlationId);
    }

    public void addTrack(EcaServiceTrack ecaServiceTrack) {
        getEcaServiceTrackTableModel().addTrack(ecaServiceTrack);
    }

    public void updateTrackStatus(String correlationId, EcaServiceTrackStatus status) {
        getEcaServiceTrackTableModel().updateTrackStatus(correlationId, status);
    }

    private EcaServiceTrackTableModel getEcaServiceTrackTableModel() {
        return (EcaServiceTrackTableModel) this.getModel();
    }

    private void initColumns() {
        TableColumn statusColumn = this.getColumnModel().getColumn(STATUS_COLUMN);
        statusColumn.setCellRenderer(new TrackStatusCellRenderer());
        statusColumn.setMinWidth(STATUS_COLUMN_WIDTH);
        statusColumn.setPreferredWidth(STATUS_COLUMN_WIDTH);

        TableColumn detailsColumn = getColumnModel().getColumn(DETAILS_COLUMN);
        detailsColumn.setCellRenderer(new TrackDetailsRenderer());
        detailsColumn.setPreferredWidth(DETAILS_COLUMN_WIDTH);
        detailsColumn.setMinWidth(DETAILS_COLUMN_WIDTH);

        TableColumn requestTypeColumn = getColumnModel().getColumn(REQUEST_TYPE_COLUMN);
        requestTypeColumn.setPreferredWidth(REQUEST_TYPE_COLUMN_WIDTH);
        requestTypeColumn.setMinWidth(REQUEST_TYPE_COLUMN_WIDTH);

        TableColumn relationNameColumn = getColumnModel().getColumn(RELATION_NAME_COLUMN);
        relationNameColumn.setPreferredWidth(RELATION_NAME_COLUMN_WIDTH);
        relationNameColumn.setMinWidth(RELATION_NAME_COLUMN_WIDTH);
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

    /**
     * Track details renderer.
     */
    private class TrackDetailsRenderer extends JTextField implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            GuiUtils.updateForegroundAndBackGround(this, table, isSelected);
            EcaServiceTrack ecaServiceTrack = getEcaServiceTrackTableModel().getTrack(row);
            if (ecaServiceTrack.getAdditionalData() != null && !ecaServiceTrack.getAdditionalData().isEmpty()) {
                this.setToolTipText(
                        ReportGenerator.getClassifierInputOptionsAsHtml(ecaServiceTrack.getAdditionalData()));
            } else {
                this.setToolTipText(null);
            }
            this.setText(Optional.ofNullable(value).map(Object::toString).orElse(null));
            this.setBorder(null);
            this.setFont(EcaServiceTrackTable.this.getFont());
            return this;
        }
    }

}
