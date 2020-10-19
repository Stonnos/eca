package eca.neural.functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link IsruFunction} class.
 *
 * @author Roman Batygin
 */
class IsruFunctionTest {

    @Test
    void testIsruFunction() {
        IsruFunction isruFunction = new IsruFunction();
        double result = isruFunction.process(0.8d);
        assertEquals(0.6246d, result, 0.0001d);
    }
}
