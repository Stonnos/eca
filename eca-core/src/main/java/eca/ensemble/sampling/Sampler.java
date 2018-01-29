/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.sampling;

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
     * Generates training sub sample based on sampling type.
     *
     * @param samplingMethod {@link SamplingMethod} object
     * @param data           {@link Instances} object
     * @param random         {@link Random} object
     * @return training sub sample based on sampling type
     */
    public static Instances instances(SamplingMethod samplingMethod, final Instances data, final Random random) {
        return samplingMethod.handle(new SamplingMethodTypeVisitor<Instances>() {
            @Override
            public Instances caseInitial() {
                return initial(data);
            }

            @Override
            public Instances caseBagging() {
                return bootstrap(data, random);
            }

            @Override
            public Instances caseRandom() {
                return random(data, random);
            }

            @Override
            public Instances caseRandomBagging() {
                return createBag(data, size(data, random), random);
            }
        });
    }

    /**
     * Generates training sub sample with <tt>K</tt> input attributes
     * based on sampling type.
     *
     * @param samplingMethod {@link SamplingMethod} object
     * @param data           {@link Instances} object
     * @param random         {@link Random} object
     * @param numAttr        number of input attributes
     * @return {@link Instances} object
     */
    public static Instances instances(SamplingMethod samplingMethod, final Instances data, final int numAttr,
                               final Random random) {
        return samplingMethod.handle(new SamplingMethodTypeVisitor<Instances>() {
            @Override
            public Instances caseInitial() {
                return initial(data, numAttr, random);
            }

            @Override
            public Instances caseBagging() {
                return bootstrap(data, numAttr, data.numInstances(), random);
            }

            @Override
            public Instances caseRandom() {
                return random(data, numAttr, random);
            }

            @Override
            public Instances caseRandomBagging() {
                return bootstrap(data, numAttr, size(data, random), random);
            }
        });
    }

    /**
     * Returns initial training set.
     *
     * @param data <tt>Instances</tt> object
     * @return initial training set
     */
    public static Instances initial(Instances data) {
        return new Instances(data);
    }

    /**
     * Returns bootstrap sample based on training set.
     *
     * @param data   {@link Instances} object
     * @param random {@link Random} object
     * @return bootstrap sample based on training set
     */
    public static Instances bootstrap(Instances data, Random random) {
        return createBag(data, data.numInstances(), random);
    }

    /**
     * Generates sub sample of random size uniformly distributed in interval [1, N],
     * where N is training set size.
     *
     * @param data   {@link Instances} object
     * @param random {@link Random} object
     * @return sub sample of random size
     */
    public static Instances random(Instances data, Random random) {
        return createRandom(data, size(data, random), random);
    }

    /**
     * Generates initial sub sample with K random attributes.
     *
     * @param data    {@link Instances} object
     * @param random  {@link Random} object
     * @param numAttr number of input attributes
     * @return sub sample with K random attributes
     */
    public static Instances initial(Instances data, int numAttr, Random random) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr, random);
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
     * @param data    {@link Instances} object
     * @param random  {@link Random} object
     * @param numAttr number of input attributes
     * @param size    bootstrap sample size
     * @return bootstrap sample with K random attributes and given size
     */
    public static Instances bootstrap(Instances data, int numAttr, int size, Random random) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr, random);
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
     * @param data    {@link Instances} object
     * @param random  {@link Random} object
     * @param numAttr number of input attributes
     * @return sub sample with K random attributes and random size
     */
    public static Instances random(Instances data, int numAttr, Random random) {
        ArrayList<Attribute> attr = randomAttributes(data, numAttr, random);
        int size = size(data, random);
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

    private static ArrayList<Attribute> randomAttributes(Instances data, int numAttr, Random random) {
        ArrayList<Attribute> attr = new ArrayList<>(numAttr);
        int k = numAttr;
        while (k != 0) {
            int i = random.nextInt(data.numAttributes());
            Attribute a = data.attribute(i);
            if (i != data.classIndex() && !attr.contains(a)) {
                attr.add((Attribute) a.copy());
                k--;
            }
        }
        attr.add((Attribute) data.classAttribute().copy());
        return attr;
    }

    private static int size(Instances data, Random random) {
        return random.nextInt(data.numInstances()) + 1;
    }

    private static void addInstance(Instances data, Instances sample, int i) {
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

    private static Instances createBag(Instances data, int size, Random random) {
        Instances bag = new Instances(data, size);
        for (int j = 0; j < size; j++) {
            bag.add(data.instance(random.nextInt(data.numInstances())));
        }
        return bag;
    }

    private static Instances createRandom(Instances data, int size, Random random) {
        Instances sample = new Instances(data, size);
        HashSet<Integer> index = new HashSet<>();
        int k = size;
        while (k != 0) {
            int i = random.nextInt(data.numInstances());
            if (index.add(i)) {
                sample.add(data.instance(i));
                k--;
            }
        }
        return sample;
    }

}
