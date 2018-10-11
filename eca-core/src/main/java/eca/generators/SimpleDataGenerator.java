package eca.generators;

import org.apache.commons.lang3.StringUtils;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Class for generating data with given properties.
 * <p>
 * Valid options are: <p>
 * <p>
 * Set number of classes (Default: 2) <p>
 * <p>
 * Set number of attributes (Default: 12) <p>
 * <p>
 * Set number of instances (Default: 100) <p>
 * <p>
 *
 * @author Roman Batygin
 */
public class SimpleDataGenerator implements DataGenerator {

    private static final String DEFAULT_RELATION_NAME_FORMAT = "GeneratedData%d";
    private static final String CLASS_NAME = "class";
    private static final String CLASS_PREFIX = "c";
    private static final String ATTRIBUTE_PREFIX = "a";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final int MAXIMUM_NUMBER_OF_CATEGORIES = 6;
    private static final double MIN_MEAN_THRESHOLD = 0;
    private static final double MAX_MEAN_THRESHOLD = 10;
    private static final double MIN_VARIANCE_THRESHOLD = 1;
    private static final double MAX_VARIANCE_THRESHOLD = 4;
    private static final int MIN_ATTRIBUTES = 2;
    private static final int MIN_CLASSES = 2;
    private static final int MIN_OBJECTS = 10;

    private static final int[] ATTRIBUTE_TYPES = {Attribute.NOMINAL, Attribute.NUMERIC};

    /**
     * Relation name
     */
    private String relationName;

    /**
     * Number of classes
     **/
    private int numClasses = 2;

    /**
     * Number of attributes
     **/
    private int numAttributes = 12;

    /**
     * Number of instances
     **/
    private int numInstances = 100;

    private Random random = new Random();

    private double[] means;

    private double[] variances;

    /**
     * Returns relation name.
     *
     * @return relation name
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Sets relation name.
     *
     * @param relationName - relation name
     */
    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    /**
     * Returns the number of classes.
     *
     * @return the number of classes
     */
    public int getNumClasses() {
        return numClasses;
    }

    /**
     * Sets the number of classes.
     *
     * @param numClasses the number of classes
     */
    public void setNumClasses(int numClasses) {
        if (numClasses < MIN_CLASSES) {
            throw new IllegalArgumentException(
                    String.format(DataGeneratorDictionary.INVALID_CLASSES_FORMAT, MIN_CLASSES));
        }
        this.numClasses = numClasses;
    }

    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes
     */
    public int getNumAttributes() {
        return numAttributes;
    }

    /**
     * Sets the number of attributes.
     *
     * @param numAttributes the number of attributes
     */
    public void setNumAttributes(int numAttributes) {
        if (numAttributes < MIN_ATTRIBUTES) {
            throw new IllegalArgumentException(
                    String.format(DataGeneratorDictionary.INVALID_ATTRIBUTES_FORMAT, MIN_ATTRIBUTES));
        }
        this.numAttributes = numAttributes;
    }

    /**
     * Returns the number of instances.
     *
     * @return the number of instances
     */
    public int getNumInstances() {
        return numInstances;
    }

    /**
     * Sets the number of instances.
     *
     * @param numInstances the number of instances
     */
    public void setNumInstances(int numInstances) {
        if (numInstances < MIN_OBJECTS) {
            throw new IllegalArgumentException(
                    String.format(DataGeneratorDictionary.INVALID_OBJECTS_FORMAT, MIN_OBJECTS));
        }
        this.numInstances = numInstances;
    }

    /**
     * Returns <tt>Random</tt> object.
     *
     * @return {@link Random} object
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Sets <tt>Random</tt> object.
     *
     * @param random {@link Random} object
     */
    public void setRandom(Random random) {
        Objects.requireNonNull(random, "Random object isn't specified!");
        this.random = random;
    }

    @Override
    public Instances generate() {
        Instances instances = new Instances(generateRelationName(), generateAttributes(), numInstances);
        instances.setClassIndex(instances.numAttributes() - 1);

        for (int i = 0; i < numInstances; i++) {
            Instance obj = new DenseInstance(instances.numAttributes());
            for (int j = 0; j < instances.numAttributes(); j++) {
                Attribute attribute = instances.attribute(j);
                double value;
                if (attribute.isNumeric()) {
                    value = NumberGenerator.nextGaussianWithNoise(random, means[j], variances[j]);
                } else {
                    value = random.nextInt(attribute.numValues());
                }
                obj.setValue(attribute, value);
            }
            instances.add(obj);
        }

        return instances;
    }

    private String generateRelationName() {
        return StringUtils.isEmpty(relationName) ?
                String.format(DEFAULT_RELATION_NAME_FORMAT, System.currentTimeMillis()) : relationName;
    }

    private ArrayList<Attribute> generateAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(numAttributes);
        means = new double[numAttributes];
        variances = new double[numAttributes];
        for (int i = 0; i < numAttributes - 1; i++) {
            means[i] = NumberGenerator.random(random, MIN_MEAN_THRESHOLD, MAX_MEAN_THRESHOLD);
            variances[i] = NumberGenerator.random(random, MIN_VARIANCE_THRESHOLD, MAX_VARIANCE_THRESHOLD);
            attributes.add(generateAttribute(ATTRIBUTE_PREFIX + i, random.nextInt(ATTRIBUTE_TYPES.length)));
        }
        attributes.add(createNominalAttribute(CLASS_NAME, CLASS_PREFIX, numClasses));
        return attributes;
    }

    private Attribute generateAttribute(String name, int type) {
        Attribute attribute;
        switch (type) {
            case Attribute.NUMERIC:
                attribute = new Attribute(name);
                break;
            case Attribute.NOMINAL:
                attribute = createNominalAttribute(name, ATTRIBUTE_VALUE,
                        random.nextInt(MAXIMUM_NUMBER_OF_CATEGORIES - 1) + 2);
                break;

            default:
                throw new IllegalArgumentException(String.format("Unexpected attribute type: %d", type));
        }
        return attribute;
    }

    private Attribute createNominalAttribute(String name, String prefix, int numValues) {
        ArrayList<String> values = new ArrayList<>(numValues);
        for (int i = 0; i < numValues; i++) {
            values.add(prefix + i);
        }
        return new Attribute(name, values);
    }

}
