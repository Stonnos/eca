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

    @Test
    void testNegativeCoefficient() {
        LogisticFunction logisticFunction = new LogisticFunction();
        assertThrows(IllegalArgumentException.class, () -> logisticFunction.setCoefficient(-1.0d));
    }

    @Test
    void testLogisticFunction() {
        LogisticFunction logisticFunction = new LogisticFunction(2d);
        double result = logisticFunction.process(0.65d);
        assertEquals(0.7858d, result, 0.0001d);
    }
}
