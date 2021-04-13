package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link IsruFunction} class.
 *
 * @author Roman Batygin
 */
class IsruFunctionTest {

    private static final double TEST_VALUE = 0.8d;

    private IsruFunction isruFunction = new IsruFunction();

    @Test
    void testIsruFunction() {
        double result = isruFunction.process(TEST_VALUE);
        assertEquals(0.6246d, result, 0.0001d);
    }

    @Test
    void testIsruFunctionDerivative() {
        double result = isruFunction.derivative(TEST_VALUE);
        assertEquals(0.4761d, result, 0.0001d);
    }
}
