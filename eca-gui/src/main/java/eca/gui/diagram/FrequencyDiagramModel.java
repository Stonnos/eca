package eca.gui.diagram;

import eca.statistics.diagram.FrequencyData;
import lombok.Data;
import org.jfree.chart.JFreeChart;
import weka.core.Attribute;

import java.util.List;
import java.util.Map;

/**
 * Frequency diagram model.
 *
 * @author Roman Batygin
 */
@Data
public class FrequencyDiagramModel {

    private Attribute attribute;

    private Map<DiagramType, JFreeChart> diagramMap;

    private List<FrequencyData> frequencyDataList;

    private DiagramType currentDiagramType;

    private JFreeChart currentChart;
}
