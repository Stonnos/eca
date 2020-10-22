package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ExponentialFunction} class.
 *
 * @author Roman Batygin
 */
class ExponentialFunctionTest {

    private static final double TEST_VALUE = 0.65d;

    private ExponentialFunction exponentialFunction = new ExponentialFunction(2d);

    @Test
    void testExponentialFunction() {
        double result = exponentialFunction.process(TEST_VALUE);
        assertEquals(0.8997d, result, 0.0001d);
    }

    @Test
    void testExponentialFunctionDerivative() {
        double result = exponentialFunction.derivative(TEST_VALUE);
        assertEquals(-0.2924d, result, 0.0001d);
    }
}
