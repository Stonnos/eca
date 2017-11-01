package eca.gui.tables;

import eca.gui.tables.models.FrequencyDiagramTableModel;
import eca.statistics.diagram.FrequencyData;

import java.util.List;

/**
 * @author Roman Batygin
 */

public class FrequencyDiagramTable extends JDataTableBase {

    public FrequencyDiagramTable(List<FrequencyData> frequencyDataList, int digits) {
        super(new FrequencyDiagramTableModel(frequencyDataList, digits));
        this.setAutoResizeOff(false);
    }
}
