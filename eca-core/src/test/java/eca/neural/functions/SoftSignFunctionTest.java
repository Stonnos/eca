package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SoftSignFunction} class.
 *
 * @author Roman Batygin
 */
class SoftSignFunctionTest {

    private static final double TEST_VALUE = 0.9d;

    private SoftSignFunction logisticFunction = new SoftSignFunction();

    @Test
    void testSoftSignFunction() {
        double result = logisticFunction.process(TEST_VALUE);
        assertEquals(0.4736d, result, 0.0001d);
    }

    @Test
    void testSoftSignFunctionDerivative() {
        double result = logisticFunction.derivative(0.9d);
        assertEquals(0.2770d, result, 0.0001d);
    }
}
