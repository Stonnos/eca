/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements ensemble classification results aggregating for
 * random subspaces method (random attributes).
 * @author Рома
 */
public class SubspacesAggregator extends Aggregator {

    /** Instances list **/
    private ArrayList<Instances> instances;

    /**
     * Creates <tt>SubspacesAggregator</tt> object.
     * @param classifier <tt>IterativeEnsembleClassifier</tt> object
     */
    public SubspacesAggregator(IterativeEnsembleClassifier classifier) {
        super(classifier);
        instances = new ArrayList<>(classifier.getIterationsNum());
    }

    /**
     * Adds <tt>Instances</tt> object to collection.
     * @param ins <tt>Instances</tt> object
     */
    public void setInstances(Instances ins) {
        instances.add(ins);
    }

    /**
     * Return <tt>Instances</tt> object at the specified position in this collection.
     * @param i index of the element
     * @return tt>Instances</tt> object at the specified position in this collection
     */
    public Instances getInstances(int i) {
        return instances.get(i);
    }

    @Override
    public double classifyInstance(int i, Instance obj) throws Exception {
        Instance o = getObject(obj, i);
        return classifier().classifiers.get(i).classifyInstance(o);
    }

    @Override
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        Instance o = getObject(obj, i);
        return classifier().classifiers.get(i).distributionForInstance(o);
    }

    private Instance getObject(Instance obj, int i) {
        Instances sample = instances.get(i);
        Instance o = new DenseInstance(sample.numAttributes());
        o.setDataset(sample);
        for (int j = 0; j < sample.numAttributes(); j++) {
            Attribute a = sample.attribute(j);
            o.setValue(a, obj.value(classifier().getData().attribute(a.name())));
        }
        return o;
    }
}
