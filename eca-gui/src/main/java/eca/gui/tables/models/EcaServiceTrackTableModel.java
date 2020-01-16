package eca.gui.tables.models;

import eca.model.EcaServiceTrack;
import eca.model.EcaServiceTrackStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Eca - service track table model.
 *
 * @author Roman Batygin
 */
public class EcaServiceTrackTableModel extends AbstractTableModel {

    public static final int REQUEST_TYPE_COLUMN = 0;
    public static final int DETAILS_COLUMN = 1;
    public static final int RELATION_NAME_COLUMN = 2;
    public static final int STATUS_COLUMN = 3;

    private static final String[] TITLE = {"Тип запроса", "Информация об запросе", "Обучающая выборка", "Статус"};

    private final Object lifecycleMonitor = new Object();

    private Map<String, EcaServiceTrackWrapper> ecaServiceTrackMap = newHashMap();

    private List<EcaServiceTrack> ecaServiceTracks = newArrayList();

    @Data
    @AllArgsConstructor
    private static class EcaServiceTrackWrapper {
        int row;
        EcaServiceTrack track;
    }

    public EcaServiceTrack getTrack(int row) {
        return ecaServiceTracks.get(row);
    }

    public EcaServiceTrack getTrack(String correlationId) {
        return getEcaServiceTrackWrapper(correlationId).getTrack();
    }

    public void addTrack(EcaServiceTrack ecaServiceTrack) {
        synchronized (lifecycleMonitor) {
            ecaServiceTracks.add(ecaServiceTrack);
            int insertedRow = getRowCount() - 1;
            ecaServiceTrackMap.put(ecaServiceTrack.getCorrelationId(),
                    new EcaServiceTrackWrapper(insertedRow, ecaServiceTrack));
            fireTableRowsInserted(insertedRow, insertedRow);
        }
    }

    public void updateTrackStatus(String correlationId, EcaServiceTrackStatus status) {
        synchronized (lifecycleMonitor) {
            EcaServiceTrackWrapper ecaServiceTrack = getEcaServiceTrackWrapper(correlationId);
            ecaServiceTrack.getTrack().setStatus(status);
            fireTableRowsUpdated(ecaServiceTrack.getRow(), ecaServiceTrack.getRow());
        }
    }

    @Override
    public int getColumnCount() {
        return TITLE.length;
    }

    @Override
    public int getRowCount() {
        return ecaServiceTracks.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        EcaServiceTrack ecaServiceTrack = ecaServiceTracks.get(row);
        switch (column) {
            case REQUEST_TYPE_COLUMN:
                return ecaServiceTrack.getRequestType().getDescription();
            case DETAILS_COLUMN:
                return ecaServiceTrack.getDetails();
            case RELATION_NAME_COLUMN:
                return ecaServiceTrack.getRelationName();
            case STATUS_COLUMN:
                return ecaServiceTrack.getStatus().getDescription();
            default:
                throw new IllegalArgumentException(String.format("Unexpected column number %d", column));
        }
    }

    @Override
    public String getColumnName(int column) {
        return TITLE[column];
    }

    private EcaServiceTrackWrapper getEcaServiceTrackWrapper(String correlationId) {
        EcaServiceTrackWrapper ecaServiceTrackIndex = ecaServiceTrackMap.get(correlationId);
        if (ecaServiceTrackIndex == null) {
            throw new IllegalStateException(
                    String.format("Can't find eca - service track with correlation id [%s]", correlationId));
        }
        return ecaServiceTrackIndex;
    }
}
