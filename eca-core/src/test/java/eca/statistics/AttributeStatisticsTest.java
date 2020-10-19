package eca.statistics;

import eca.text.NumericFormatFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link AttributeStatistics} class.
 *
 * @author Roman Batygin
 */
class AttributeStatisticsTest {

    private static final String X1_ATTRIBUTE = "x1";

    private Instances instances;

    private AttributeStatistics attributeStatistics;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
        attributeStatistics = new AttributeStatistics(instances, NumericFormatFactory.getInstance());
    }

    @Test
    void tesGetMin() {
        assertEquals(4.3d, attributeStatistics.getMin(instances.attribute(X1_ATTRIBUTE)));
    }

    @Test
    void tesGetMax() {
        assertEquals(7.9d, attributeStatistics.getMax(instances.attribute(X1_ATTRIBUTE)));
    }
}
