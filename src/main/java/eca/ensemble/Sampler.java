/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import java.util.ArrayList;
import java.util.HashSet;
import weka.core.Instances;
import java.util.Random;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Class for generating training data.
 * @author Рома
 */
public class Sampler implements java.io.Serializable {

    /** Initial sample type **/
    public static final int INITIAL = 0;

    /** Bootstrap sample type **/
    public static final int BAGGING = 1;

    /** Random sub sample type **/
    public static final int RANDOM = 2;

    /** Random bootstrap sub sample type **/
    public static final int RANDOM_BAGGING = 3;

    /** Sampling type **/
    private int sampling = INITIAL;

    private Random random = new Random();

    /**
     * Returns sampling method type.
     * @return sampling method type
     */
    public int getSampling() {
        return sampling;
    }

    /**
     * Returns sampling method description.
     * @return sampling method description
     */
    public String getDescription() {
        String info = null;
        switch (sampling) {
            case INITIAL: info = "Исходное обучающее множество"; break;
            case BAGGING: info = "Бутстрэп выборки"; break;
            case RANDOM: info = "Случайные подвыборки"; break;
            case RANDOM_BAGGING: info = "Бутстрэп выборки случайного размера"; break;
        }
        return info;
    }

    /**
     * Sets sampling method type.
     * @param sampling sampling method type
     * @exception IllegalArgumentException if the specified sampling type
     * is invalid
     */
    public void setSampling(int sampling) {
        if (sampling != INITIAL && sampling != BAGGING
            && sampling != RANDOM && sampling != RANDOM_BAGGING)
            throw new IllegalArgumentException("Wrong sampling value!");
        this.sampling = sampling;
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

    /**
     * Generates training sub sample based on sampling type.
     * @param data <tt>Instances</tt> object
     * @return training sub sample based on sampling type
     */
    public Instances instances(Instances data) {
        Instances sample = null;
        switch (sampling) {
            case INITIAL: sample = initial(data); break;
            case BAGGING: sample = bootstrap(data); break;
            case RANDOM: sample = random(data); break;
            case RANDOM_BAGGING: sample = createBag(data, size(data)); break;
        }
        return sample;
    }

    /**
     * Generates training sub sample with <tt>K</tt> input attributes
     * based on sampling type.
     * @param data <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @return
     */
    public Instances instances(Instances data, int numAttr) {
        Instances sample = null;
        switch (sampling) {
            case INITIAL: sample = initial(data, numAttr); break;
            case BAGGING: sample = bootstrap(data, numAttr, data.numInstances()); break;
            case RANDOM: sample = random(data, numAttr); break;
            case RANDOM_BAGGING: sample = bootstrap(data, numAttr, size(data)); break;
        }
        return sample;
    }

    /**
     * Returns initial training set.
     * @param data <tt>Instances</tt> object
     * @return initial training set
     */
    public Instances initial(Instances data) {
        return data;
    }

    /**
     * Returns bootstrap sample based on training set.
     * @param data <tt>Instances</tt> object
     * @return bootstrap sample based on training set
     */
    public Instances bootstrap(Instances data) {
        return createBag(data, data.numInstances());
    }

    /**
     * Generates sub sample of random size uniformly distributed in interval [1, N],
     * where N is training set size.
     * @param data <tt>Instances</tt> object
     * @return sub sample of random size
     */
    public Instances random(Instances data) {
        return createRandom(data, size(data));
    }

    /**
     * Generates initial sub sample with K random attributes.
     * @param data <tt>Instances</tt> object
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
     * @param data <tt>Instances</tt> object
     * @param numAttr number of input attributes
     * @param size bootstrap sample size
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
     * @param data <tt>Instances</tt> object
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
                attr.add((Attribute)a.copy());
                numAttr--;
            }
        }
        attr.add((Attribute)data.classAttribute().copy());
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
            }
            else {
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
