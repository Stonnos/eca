package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link HyperbolicTangentFunction} class.
 *
 * @author Roman Batygin
 */
class HyperbolicTangentFunctionTest {

    @Test
    void testHyperbolicTangentFunction() {
        HyperbolicTangentFunction hyperbolicTangentFunction = new HyperbolicTangentFunction();
        double result = hyperbolicTangentFunction.process(0.5d);
        assertEquals(0.4621d, result, 0.0001d);
    }
}
