package eca.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import weka.core.Instances;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link SimpleDataGenerator} class.
 *
 * @author Roman Batygin
 */
class SimpleDataGeneratorTest {

    private static final int SEED = 1;
    private static final String RELATION_NAME = "test-data";
    private static final int NUM_ATTRIBUTES = 10;
    private static final int NUM_CLASSES = 2;
    private static final int NUM_INSTANCES = 200;

    private final SimpleDataGenerator simpleDataGenerator = new SimpleDataGenerator();

    @BeforeEach
    void init() {
        simpleDataGenerator.setRandom(new Random(SEED));
        simpleDataGenerator.setNumAttributes(NUM_ATTRIBUTES);
        simpleDataGenerator.setNumClasses(NUM_CLASSES);
        simpleDataGenerator.setNumInstances(NUM_INSTANCES);
        simpleDataGenerator.setRelationName(RELATION_NAME);
    }

    @Test
    void testGenerateData() {
        Instances generated = simpleDataGenerator.generate();
        assertNotNull(generated);
        assertEquals(RELATION_NAME, generated.relationName());
        assertEquals(NUM_ATTRIBUTES, generated.numAttributes());
        assertEquals(NUM_INSTANCES, generated.numInstances());
        assertEquals(NUM_CLASSES, generated.numClasses());
    }
}
