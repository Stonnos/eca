package eca.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;
import weka.core.Utils;

import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link InstancesStatistics} class.
 */
class InstancesStatisticsTest {

    private static final String DATA_IRIS_XLS = "data/iris.xls";

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
    }

    @Test
    void testNumNumericAttributes() {
        assertEquals(4, InstancesStatistics.numNumericAttributes(instances));
    }

    @Test
    void testNumNominalAttributes() {
        assertEquals(1, InstancesStatistics.numNominalAttributes(instances));
    }

    @Test
    void testNoMissingValues() {
        assertFalse(InstancesStatistics.hasMissing(instances));
    }

    @Test
    void testHasMissingValues() {
        instances.firstInstance().setValue(0, Utils.missingValue());
        assertTrue(InstancesStatistics.hasMissing(instances));
    }
}
