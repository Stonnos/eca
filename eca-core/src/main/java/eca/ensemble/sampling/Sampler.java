/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.sampling;

import org.springframework.util.Assert;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * Class for generating training data.
 *
 * @author Roman Batygin
 */
public class Sampler implements java.io.Serializable {

    /**
     * Sampling type
     **/
    private SamplingMethod samplingMethod = SamplingMethod.INITIAL;

    private Random random = new Random();

    /**
     * Returns sampling method type.
     *
     * @return sampling method type
     */
    public SamplingMethod getSamplingMethod() {
        return samplingMethod;
    }

    /**
     * Returns sampling method description.
     *
     * @return sampling method description
     */
    public String getDescription() {
        return samplingMethod.getDescription();
    }

    /**
     * Sets sampling method type.
     *
     * @param samplingMethod sampling method type
     * @throws IllegalArgumentException if the specified sampling type
     *                                  is null
     */
    public void setSamplingMethod(SamplingMethod samplingMethod) {
        Assert.notNull(samplingMethod, "Sampling method is not specified!");
        this.samplingMethod = samplingMethod;
    }

    /**
     * Returns <tt>Random</tt> object.
     *
     * @return <tt>Random</tt> object
     */
    public Random getRandom() {
        return random;
    }

    /**
     * Sets <tt>Random</tt> object.
     *
     * @param random <tt>Random</tt> object
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Generates training sub sample based on sampling type.
     *
     * @param data <tt>Instances</tt> object
     * @return training sub sample based on sampling type
     */
    public Instances instances(final Instances data) {

        return getSamplingMethod().handle(new SamplingMethodTypeVisitor<Instances>() {
            @Override
            public Instances caseInitial() {
                return initial(data);
            }

            @Override
            public Instances caseBagging() {
                return bootstrap(data);
            }

            @Override
            public Instances caseRandom() {
                return random(data);
            }

            @Override
            public Instances caseRandomBagging() {
                return createBag(data, size(data));
            }
        });
    }

    /**
     * Generates training sub sample with <tt>K</tt> input attributes
     * based on sampling type.
     *
     * @param data    <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @return
     */
    public Instances instances(final Instances data, final int numAttr) {

        return getSamplingMethod().handle(new SamplingMethodTypeVisitor<Instances>() {
            @Override
            public Instances caseInitial() {
                return initial(data, numAttr);
            }

            @Override
            public Instances caseBagging() {
                return bootstrap(data, numAttr, data.numInstances());
            }

            @Override
            public Instances caseRandom() {
                return random(data, numAttr);
            }

            @Override
            public Instances caseRandomBagging() {
                return bootstrap(data, numAttr, size(data));
            }
        });
    }

    /**
     * Returns initial training set.
     *
     * @param data <tt>Instances</tt> object
     * @return initial training set
     */
    public Instances initial(Instances data) {
        return data;
    }

    /**
     * Returns bootstrap sample based on training set.
     *
     * @param data <tt>Instances</tt> object
     * @return bootstrap sample based on training set
     */
    public Instances bootstrap(Instances data) {
        return createBag(data, data.numInstances());
    }

    /**
     * Generates sub sample of random size uniformly distributed in interval [1, N],
     * where N is training set size.
     *
     * @param data <tt>Instances</tt> object
     * @return sub sample of random size
     */
    public Instances random(Instances data) {
        return createRandom(data, size(data));
    }

    /**
     * Generates initial sub sample with K random attributes.
     *
     * @param data    <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @return
     */
    public Instances initial(Instances data, int numAttr) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr);
        Instances sample = new Instances(data.relationName(), attr, data.numInstances());
        for (int i = 0; i < data.numInstances(); i++) {
            addInstance(data, sample, i);
        }
        sample.setClass(sample.attribute(data.classAttribute().name()));
        return sample;
    }

    /**
     * Generates bootstrap sample with K random attributes and given size.
     *
     * @param data    <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @param size    bootstrap sample size
     * @return bootstrap sample with K random attributes and given size
     */
    public Instances bootstrap(Instances data, int numAttr, int size) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr);
        Instances sample = new Instances(data.relationName(), attr, size);
        for (int i = 0; i < size; i++) {
            addInstance(data, sample, random.nextInt(data.numInstances()));
        }
        sample.setClass(sample.attribute(data.classAttribute().name()));
        return sample;
    }

    /**
     * Generates sub sample with K random attributes and random size uniformly
     * distributed in interval [1, N], where N is training set size.
     *
     * @param data    <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @return sub sample with K random attributes and random size
     */
    public Instances random(Instances data, int numAttr) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr);
        int size = size(data);
        Instances sample = new Instances(data.relationName(), attr, size);
        HashSet<Integer> index = new HashSet<>();
        while (size != 0) {
            int i = random.nextInt(data.numInstances());
            if (index.add(i)) {
                addInstance(data, sample, i);
                size--;
            }
        }
        sample.setClass(sample.attribute(data.classAttribute().name()));
        return sample;
    }

    private ArrayList<Attribute> randomAttributes(Instances data, int numAttr) {
        ArrayList<Attribute> attr = new ArrayList<>(numAttr);
        while (numAttr != 0) {
            int i = random.nextInt(data.numAttributes());
            Attribute a = data.attribute(i);
            if (i != data.classIndex() && !attr.contains(a)) {
                attr.add((Attribute) a.copy());
                numAttr--;
            }
        }
        attr.add((Attribute) data.classAttribute().copy());
        return attr;
    }

    private int size(Instances data) {
        return random.nextInt(data.numInstances()) + 1;
    }

    private void addInstance(Instances data, Instances sample, int i) {
        Instance obj = new DenseInstance(sample.numAttributes());
        for (int j = 0; j < sample.numAttributes(); j++) {
            Attribute a = sample.attribute(j);
            if (a.isNumeric()) {
                obj.setValue(a, data.instance(i).value(data.attribute(a.name())));
            } else {
                obj.setValue(a, data.instance(i).stringValue(data.attribute(a.name())));
            }
        }
        sample.add(obj);
    }

    private Instances createBag(Instances data, int size) {
        Instances bag = new Instances(data, size);
        for (int j = 0; j < size; j++) {
            bag.add(data.instance(random.nextInt(data.numInstances())));
        }
        return bag;
    }

    private Instances createRandom(Instances data, int size) {
        Instances sample = new Instances(data, size);
        HashSet<Integer> index = new HashSet<>();
        while (size != 0) {
            int i = random.nextInt(data.numInstances());
            if (index.add(i)) {
                sample.add(data.instance(i));
                size--;
            }
        }
        return sample;
    }

}
