package eca.statistics.diagram;

import eca.statistics.AttributeStatistics;
import eca.text.NumericFormatFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Attribute;
import weka.core.Instances;

import java.util.List;
import java.util.stream.IntStream;

import static eca.AssertionUtils.assertFrequencyData;
import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link FrequencyDiagramBuilder} class.
 *
 * @author Roman Batygin
 */
class FrequencyDiagramBuilderTest {

    private static final String CLASS_ATTRIBUTE = "class";
    private static final String X1_ATTRIBUTE = "x1";

    private Instances instances;

    private AttributeStatistics attributeStatistics;
    private FrequencyDiagramBuilder frequencyDiagramBuilder;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
        attributeStatistics = new AttributeStatistics(instances, NumericFormatFactory.getInstance());
        frequencyDiagramBuilder = new FrequencyDiagramBuilder(attributeStatistics);
    }

    @Test
    void testFrequencyDiagramForNumericAttribute() {
        Attribute attribute = instances.attribute(X1_ATTRIBUTE);
        List<FrequencyData> frequencyData =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNumericAttribute(attribute);
        assertNotNull(frequencyData);
        assertEquals( 8, frequencyData.size());
        assertFrequencyData(frequencyData.get(0), 4.3d, 4.75d, 11);
        assertFrequencyData(frequencyData.get(1), 4.75d, 5.2d, 34);
        assertFrequencyData(frequencyData.get(2), 5.2d, 5.65d, 20);
        assertFrequencyData(frequencyData.get(3), 5.65d, 6.1d, 30);
        assertFrequencyData(frequencyData.get(4), 6.1d, 6.55d, 25);
        assertFrequencyData(frequencyData.get(5), 6.55d, 7d, 18);
        assertFrequencyData(frequencyData.get(6), 7d, 7.45d, 6);
        assertFrequencyData(frequencyData.get(7), 7.45d, 7.9d, 6);
        int total = frequencyData.stream().mapToInt(FrequencyData::getFrequency).sum();
        assertEquals(instances.numInstances(), total);
    }

    @Test
    void testFrequencyDiagramForNominalAttribute() {
        Attribute attribute = instances.attribute(CLASS_ATTRIBUTE);
        List<FrequencyData> frequencyData =
                frequencyDiagramBuilder.calculateFrequencyDiagramDataForNominalAttribute(attribute);
        assertNotNull(frequencyData);
        assertEquals(frequencyData.size(), attribute.numValues());
        IntStream.range(0, attribute.numValues()).forEach(
                i -> assertEquals(attributeStatistics.getValuesNum(attribute, i), frequencyData.get(i).getFrequency()));
    }

    @Test
    void testInvalidDataForNumericFrequencyDiagram() {
        Attribute attribute = instances.attribute(CLASS_ATTRIBUTE);
        assertThrows(IllegalArgumentException.class,
                () -> frequencyDiagramBuilder.calculateFrequencyDiagramDataForNumericAttribute(attribute));
    }

    @Test
    void testInvalidDataForNominalFrequencyDiagram() {
        Attribute attribute = instances.attribute(X1_ATTRIBUTE);
        assertThrows(IllegalArgumentException.class,
                () -> frequencyDiagramBuilder.calculateFrequencyDiagramDataForNominalAttribute(attribute));
    }
}
