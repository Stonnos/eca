/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.Attribute;
import java.util.ArrayList;

/**
 *
 * @author Рома
 */
public class SubspacesAggregator extends Aggregator {

    private ArrayList<Instances> instances;

    public SubspacesAggregator(IterativeEnsembleClassifier classifier, int size) {
        super(classifier);
        instances = new ArrayList<>(size);
    }

    public void setInstances(Instances ins) {
        instances.add(ins);
    }

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
            o.setValue(a, obj.value(classifier().data().attribute(a.name())));
        }
        return o;
    }
}
