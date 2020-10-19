package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SinusoidFunction} class.
 *
 * @author Roman Batygin
 */
class SinusoidFunctionTest {

    @Test
    void testSinusoidFunction() {
        SinusoidFunction sinusoidFunction = new SinusoidFunction();
        double result = sinusoidFunction.process(0.65d);
        assertEquals(0.6051d, result, 0.0001d);
    }
}
