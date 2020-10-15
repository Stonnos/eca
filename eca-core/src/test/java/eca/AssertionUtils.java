package eca;

import eca.statistics.diagram.FrequencyData;
import lombok.experimental.UtilityClass;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Assertion utility class.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class AssertionUtils {

    private static final double DELTA = 0.00001d;

    /**
     * Asserts instances. Compare all values.
     *
     * @param expected - expected instances
     * @param actual   - actual instances
     */
    public static void assertInstances(Instances expected, Instances actual) {
        IntStream.range(0, expected.numInstances()).forEach(i -> {
            Instance expectedInstance = expected.instance(i);
            Instance actualInstance = actual.instance(i);
            IntStream.range(0, expectedInstance.numAttributes()).forEach(j -> {
                Attribute attribute = expectedInstance.attribute(j);
                switch (attribute.type()) {
                    case Attribute.NOMINAL:
                    case Attribute.DATE:
                        assertEquals(expectedInstance.stringValue(j), actualInstance.stringValue(j));
                        break;
                    case Attribute.NUMERIC:
                        assertEquals(expectedInstance.value(j), actualInstance.value(j));
                        break;
                    default:
                        fail("Expected numeric or nominal attribute type!");
                }
            });
        });
    }

    /**
     * Asserts frequency data.
     *
     * @param frequencyData      - frequency data
     * @param expectedLowerBound - expected interval lower bound
     * @param expectedUpperBound - expected interval upper bound
     * @param expectedFrequency  - expected frequency
     */
    public static void assertFrequencyData(FrequencyData frequencyData, double expectedLowerBound,
                                           double expectedUpperBound, int expectedFrequency) {
        assertEquals(expectedLowerBound, frequencyData.getLowerBound(), DELTA);
        assertEquals(expectedUpperBound, frequencyData.getUpperBound(), DELTA);
        assertEquals(expectedFrequency, frequencyData.getFrequency());
    }
}
