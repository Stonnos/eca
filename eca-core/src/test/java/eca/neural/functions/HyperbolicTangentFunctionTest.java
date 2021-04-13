package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link HyperbolicTangentFunction} class.
 *
 * @author Roman Batygin
 */
class HyperbolicTangentFunctionTest {

    private static final double TEST_VALUE = 0.5d;

    private HyperbolicTangentFunction hyperbolicTangentFunction = new HyperbolicTangentFunction();

    @Test
    void testHyperbolicTangentFunction() {
        double result = hyperbolicTangentFunction.process(TEST_VALUE);
        assertEquals(0.4621d, result, 0.0001d);
    }

    @Test
    void testHyperbolicTangentFunctionDerivative() {
        double result = hyperbolicTangentFunction.derivative(TEST_VALUE);
        assertEquals(0.7864d, result, 0.0001d);
    }
}
