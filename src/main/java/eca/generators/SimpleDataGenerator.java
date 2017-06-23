package eca.generators;

import weka.core.Instances;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class for generating data with given properties.
 *
 * Valid options are: <p>
 *
 * Set number of classes (Default: 2) <p>
 *
 * Set number of attributes (Default: 12) <p>
 *
 * Set number of instances (Default: 100) <p>
 *
 * Created by Roman93 on 15.04.2017.
 */
public class SimpleDataGenerator implements DataGenerator {

    private static final String RELATION_NAME = "GeneratedData";

    private static final int MAXIMUM_NUMBER_OF_CATEGORIES = 6;

    private static final double MIN_MEAN_THRESHOLD = 0;

    private static final double MAX_MEAN_THRESHOLD = 10;

    private static final double MIN_VARIANCE_THRESHOLD = 1;

    private static final double MAX_VARIANCE_THRESHOLD = 4;

    private static final int[] ATTRIBUTE_TYPES = {Attribute.NOMINAL, Attribute.NUMERIC};

    /** Number of classes **/
    private int numClasses = 2;

    /** Number of attributes **/
    private int numAttributes = 12;

    /** Number of instances **/
    private int numInstances = 100;

    private Random random = new Random();

    private double[] means;

    private double[] variances;

    /**
     * Returns the number of classes.
     * @return the number of classes
     */
    public int getNumClasses() {
        return numClasses;
    }

    /**
     * Sets the number of classes.
     * @param numClasses the number of classes
     */
    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    /**
     * Returns the number of attributes.
     * @return the number of attributes
     */
    public int getNumAttributes() {
        return numAttributes;
    }

    /**
     * Sets the number of attributes.
     * @param numAttributes the number of attributes
     */
    public void setNumAttributes(int numAttributes) {
        this.numAttributes = numAttributes;
    }

    /**
     * Returns the number of instances.
     * @return the number of instances
     */
    public int getNumInstances() {
        return numInstances;
    }

    /**
     * Sets the number of instances.
     * @param numInstances the number of instances
     */
    public void setNumInstances(int numInstances) {
        this.numInstances = numInstances;
    }

    /**
     * Returns <tt>Random</tt> object.
     * @return <tt>Random</tt> object
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Sets <tt>Random</tt> object.
     * @param random <tt>Random</tt> object
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public Instances generate() {
        Instances instances = new Instances(RELATION_NAME, generateAttributes(), numInstances);
        instances.setClassIndex(instances.numAttributes() - 1);

        for (int i = 0; i < numInstances; i++) {

            Instance obj = new DenseInstance(instances.numAttributes());

            for (int j = 0; j < instances.numAttributes(); j++) {
                Attribute attribute = instances.attribute(j);
                double value;
                if (attribute.isNumeric()) {
                    value = NumberGenerator.nextGaussianWithNoise(random, means[j], variances[j]);
                }
                else {
                    value = random.nextInt(attribute.numValues());
                }
                obj.setValue(attribute, value);
            }
            instances.add(obj);
        }

        return instances;
    }

    private ArrayList<Attribute> generateAttributes() {
        ArrayList<Attribute> attributes = new ArrayList<>(numAttributes);
        means = new double[numAttributes];
        variances = new double[numAttributes];

        for (int i = 0; i < numAttributes - 1; i++) {
            means[i] = NumberGenerator.random(MIN_MEAN_THRESHOLD, MAX_MEAN_THRESHOLD);
            variances[i] = NumberGenerator.random(MIN_VARIANCE_THRESHOLD, MAX_VARIANCE_THRESHOLD);
            attributes.add(generateAttribute("a" + i, random.nextInt(ATTRIBUTE_TYPES.length)));
        }

        attributes.add(createNominalAttribute("class", "c", numClasses));
        return attributes;
    }

    private Attribute generateAttribute(String name, int type) {
        Attribute attribute = null;
        switch (type) {
            case Attribute.NUMERIC: {
                attribute = new Attribute(name);
                break;
            }
            case Attribute.NOMINAL: {
                attribute = createNominalAttribute(name, "value",
                        random.nextInt(MAXIMUM_NUMBER_OF_CATEGORIES - 1) + 2);
                break;
            }
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
