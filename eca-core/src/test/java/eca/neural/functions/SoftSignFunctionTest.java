package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link SoftSignFunction} class.
 *
 * @author Roman Batygin
 */
class SoftSignFunctionTest {

    @Test
    void testSoftSignFunction() {
        SoftSignFunction logisticFunction = new SoftSignFunction();
        double result = logisticFunction.process(0.9d);
        assertEquals(0.4736d, result, 0.0001d);
    }
}
