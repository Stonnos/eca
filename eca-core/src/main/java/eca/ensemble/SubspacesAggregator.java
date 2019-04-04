/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.InstancesHandler;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.List;

/**
 * Implements ensemble classification results aggregating for
 * random subspaces method (random attributes).
 *
 * @author Roman Batygin
 */
public class SubspacesAggregator extends Aggregator {

    /**
     * Creates <tt>SubspacesAggregator</tt> object.
     *
     * @param classifiers - classifiers list
     */
    public SubspacesAggregator(List<ClassifierOrderModel> classifiers, Instances instances) {
        super(classifiers, instances);
    }

    @Override
    public double classifyInstance(int i, Instance obj) throws Exception {
        Instance o = transformToSubspaceInstance(obj, i);
        return getClassifiers().get(i).getClassifier().classifyInstance(o);
    }

    @Override
    public double[] distributionForInstance(int i, Instance obj) throws Exception {
        Instance subspaceInstance = transformToSubspaceInstance(obj, i);
        return getClassifiers().get(i).getClassifier().distributionForInstance(subspaceInstance);
    }

    private Instance transformToSubspaceInstance(Instance obj, int i) {
        Instances sample = ((InstancesHandler) getClassifiers().get(i).getClassifier()).getData();
        Instance newInstance = new DenseInstance(sample.numAttributes());
        newInstance.setDataset(sample);
        for (int j = 0; j < sample.numAttributes(); j++) {
            Attribute a = sample.attribute(j);
            newInstance.setValue(a, obj.value(getInstances().attribute(a.name())));
        }
        return newInstance;
    }
}
