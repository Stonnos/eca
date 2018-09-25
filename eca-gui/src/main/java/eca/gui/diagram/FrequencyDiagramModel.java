package eca.gui.diagram;

import eca.statistics.diagram.FrequencyData;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jfree.chart.JFreeChart;

import java.util.List;

/**
 * Frequency diagram model.
 *
 * @author Roman Batygin
 */
@Data
@AllArgsConstructor
public class FrequencyDiagramModel {

    private JFreeChart chart;

    private List<FrequencyData> frequencyDataList;

    private DiagramType diagramType;
}
