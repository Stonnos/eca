package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link ExponentialFunction} class.
 *
 * @author Roman Batygin
 */
class ExponentialFunctionTest {

    @Test
    void testExponentialFunction() {
        ExponentialFunction exponentialFunction = new ExponentialFunction(2d);
        double result = exponentialFunction.process(0.65d);
        assertEquals(0.8997d, result, 0.0001d);
    }
}
