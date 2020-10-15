package eca.roc;

import eca.core.evaluation.Evaluation;
import eca.trees.CART;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for roc curve data calculation.
 *
 * @author Roman Batygin
 */
class RocCurveTest {

    private static final String DATA_IRIS_XLS = "data/iris.xls";
    private static final String IRIS_VIRGINICA = "Iris-virginica";
    private static final double EXPECTED_SENSITIVITY_POINT = 0.98d;
    private static final double EXPECTED_100_SPECIFICITY_POINT = 0.03d;

    private Instances instances;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_IRIS_XLS);
    }

    @Test
    void testOptimalThresholdCalculation() throws Exception {
        CART cart = new CART();
        cart.buildClassifier(instances);
        Evaluation evaluation = new Evaluation(instances);
        evaluation.evaluateModel(cart, instances);
        RocCurve rocCurve = new RocCurve(evaluation);
        int classIndex = instances.classAttribute().indexOfValue(IRIS_VIRGINICA);
        Instances rocCurveData = rocCurve.getROCCurve(classIndex);
        assertNotNull(rocCurveData);
        ThresholdModel thresholdModel = rocCurve.findOptimalThreshold(rocCurveData);
        assertNotNull(thresholdModel);
        assertEquals(EXPECTED_SENSITIVITY_POINT, thresholdModel.getSensitivity());
        assertEquals(EXPECTED_100_SPECIFICITY_POINT, thresholdModel.getSpecificity());
    }
}
