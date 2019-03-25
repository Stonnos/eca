/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements ensemble classification results aggregating for
 * random subspaces method (random attributes).
 *
 * @author Roman Batygin
 */
public class SubspacesAggregator extends Aggregator {

    /**
     * Instances list
     **/
    private ArrayList<Instances> instancesList;

    /**
     * Creates <tt>SubspacesAggregator</tt> object.
     *
     * @param classifiers - classifiers list
     */
    public SubspacesAggregator(List<Classifier> classifiers, Instances instances) {
        super(classifiers, instances);
        this.instancesList = new ArrayList<>();
    }

    /**
     * Adds <tt>Instances</tt> object to collection.
     *
     * @param ins <tt>Instances</tt> object
     */
    public void addInstances(Instances ins) {
        instancesList.add(ins);
    }

    /**
     * Return <tt>Instances</tt> object at the specified position in this collection.
     *
     * @param i index of the element
     * @return tt>Instances</tt> object at the specified position in this collection
     */
    public Instances getInstances(int i) {
        return instancesList.get(i);
    }

    @Override
    public double classifyInstance(int i, Instance obj) throws Exception {
        Instance o = getObject(obj, i);
        return getClassifiers().get(i).classifyInstance(o);
    }

    @Override
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        Instance o = getObject(obj, i);
        return getClassifiers().get(i).distributionForInstance(o);
    }

    private Instance getObject(Instance obj, int i) {
        Instances sample = instancesList.get(i);
        Instance newInstance = new DenseInstance(sample.numAttributes());
        newInstance.setDataset(sample);
        for (int j = 0; j < sample.numAttributes(); j++) {
            Attribute a = sample.attribute(j);
            newInstance.setValue(a, obj.value(getInstances().attribute(a.name())));
        }
        return newInstance;
    }
}
