package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link LogisticFunction} class.
 *
 * @author Roman Batygin
 */
class LogisticFunctionTest {

    private static final double TEST_VALUE = 0.65d;

    private LogisticFunction logisticFunction = new LogisticFunction(2d);

    @Test
    void testNegativeCoefficient() {
        LogisticFunction logisticFunction = new LogisticFunction();
        assertThrows(IllegalArgumentException.class, () -> logisticFunction.setCoefficient(-1.0d));
    }

    @Test
    void testLogisticFunction() {
        double result = logisticFunction.process(TEST_VALUE);
        assertEquals(0.7858d, result, 0.0001d);
    }

    @Test
    void testLogisticFunctionDerivative() {
        double result = logisticFunction.derivative(TEST_VALUE);
        assertEquals(0.3365d, result, 0.0001d);
    }
}
