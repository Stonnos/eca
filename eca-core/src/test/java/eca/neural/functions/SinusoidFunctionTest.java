package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SinusoidFunction} class.
 *
 * @author Roman Batygin
 */
class SinusoidFunctionTest {

    private static final double TEST_VALUE = 0.65d;

    private SinusoidFunction sinusoidFunction = new SinusoidFunction();

    @Test
    void testSinusoidFunction() {
        double result = sinusoidFunction.process(TEST_VALUE);
        assertEquals(0.6051d, result, 0.0001d);
    }

    @Test
    void testSinusoidFunctionDerivative() {
        double result = sinusoidFunction.derivative(TEST_VALUE);
        assertEquals(0.7960d, result, 0.0001d);
    }
}
