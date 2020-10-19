package eca.statistics.contingency;

import eca.statistics.contingency.model.ChiSquareTestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Attribute;
import weka.core.Instances;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static eca.TestHelperUtils.DATA_CREDIT_ARFF;
import static eca.TestHelperUtils.loadInstances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for contingency table calculation.
 *
 * @author Roman Batygin
 */
class ContingencyTableTest {

    private static final String SAVINGS_STATUS_ATTRIBUTE = "savings_status";
    private static final String JOB_ATTRIBUTE = "job";
    private static final String DURATION_ATTRIBUTE = "duration";
    private static final String CLASS_ATTRIBUTE = "class";
    private static final int SCALE = 4;
    private static final double EXPECTED_ALPHA = 0.05d;

    private Instances instances;
    private ContingencyTable contingencyTable;

    @BeforeEach
    void init() {
        instances = loadInstances(DATA_CREDIT_ARFF);
        contingencyTable = new ContingencyTable(instances);
    }

    @Test
    void testChiSquareTestWithSignificantResult() {
        Attribute first = instances.attribute(SAVINGS_STATUS_ATTRIBUTE);
        Attribute second = instances.attribute(CLASS_ATTRIBUTE);
        ChiSquareTestResult chiSquareTestResult = calculateChiSquareTest(first, second);
        assertEquals(4, chiSquareTestResult.getDf());
        assertEquals(EXPECTED_ALPHA, chiSquareTestResult.getAlpha());
        BigDecimal expectedChiSquareValue =
                BigDecimal.valueOf(chiSquareTestResult.getChiSquaredValue()).setScale(SCALE, RoundingMode.HALF_UP);
        assertEquals(BigDecimal.valueOf(36.0989d), expectedChiSquareValue);
        assertTrue(chiSquareTestResult.isSignificant());
    }

    @Test
    void testChiSquareTestWithNotSignificantResult() {
        Attribute first = instances.attribute(JOB_ATTRIBUTE);
        Attribute second = instances.attribute(CLASS_ATTRIBUTE);
        ChiSquareTestResult chiSquareTestResult = calculateChiSquareTest(first, second);
        assertEquals(3, chiSquareTestResult.getDf());
        assertEquals(EXPECTED_ALPHA, chiSquareTestResult.getAlpha());
        BigDecimal expectedChiSquareValue =
                BigDecimal.valueOf(chiSquareTestResult.getChiSquaredValue()).setScale(SCALE, RoundingMode.HALF_UP);
        assertEquals(BigDecimal.valueOf(1.8852d), expectedChiSquareValue);
        assertFalse(chiSquareTestResult.isSignificant());
    }

    @Test
    void testChiSquareTestForNumericAttribute() {
        Attribute first = instances.attribute(DURATION_ATTRIBUTE);
        Attribute second = instances.attribute(CLASS_ATTRIBUTE);
        assertThrows(IllegalArgumentException.class, () -> calculateChiSquareTest(first, second));
    }

    private ChiSquareTestResult calculateChiSquareTest(Attribute first, Attribute second) {
        double[][] contingencyMatrix = contingencyTable.computeContingencyMatrix(first.index(), second.index());
        assertNotNull(contingencyMatrix);
        assertEquals(contingencyMatrix.length, first.numValues() + 1);
        assertEquals(contingencyMatrix[0].length, second.numValues() + 1);
        assertEquals(contingencyMatrix[first.numValues()][second.numValues()], instances.numInstances());
        //Calculate and compare chi square test
        ChiSquareTestResult chiSquareTestResult =
                contingencyTable.calculateChiSquaredResult(first.index(), second.index(), contingencyMatrix);
        assertNotNull(chiSquareTestResult);
        return chiSquareTestResult;
    }
}
