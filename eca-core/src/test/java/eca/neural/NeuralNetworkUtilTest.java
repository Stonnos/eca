package eca.neural;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.DATA_IRIS_XLS;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link NeuralNetworkUtil} class.
 *
 * @author Roman Batygin
 */
class NeuralNetworkUtilTest {

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
    }

    @Test
    void testMinNumNeuronsInHiddenLayer() {
        assertEquals(7, NeuralNetworkUtil.getMinNumNeuronsInHiddenLayer(instances));
    }

    @Test
    void testMaxNumNeuronsInHiddenLayer() {
        assertEquals(130, NeuralNetworkUtil.getMaxNumNeuronsInHiddenLayer(instances));
    }
}
