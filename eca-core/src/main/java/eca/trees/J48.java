package eca.trees;

import eca.core.InstancesHandler;
import eca.core.ListOptionsHandler;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

/**
 * Classifier for generating J48 decision tree model.
 *
 * @author Roman Batygin
 */
public class J48 extends weka.classifiers.trees.J48 implements InstancesHandler, ListOptionsHandler {

    private Instances data;

    @Override
    public void buildClassifier(Instances data) throws Exception {
        this.data = data;
        super.buildClassifier(data);
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public String[] getOptions() {
        List<String> options = getListOptions();
        return options.toArray(new String[options.size()]);
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = new ArrayList<>();
        options.add(DecisionTreeDictionary.MIN_NUM_OBJECTS_IN_LEAF);
        options.add(String.valueOf(getMinNumObj()));
        options.add(DecisionTreeDictionary.BINARY_TREE);
        options.add(String.valueOf(getBinarySplits()));
        options.add(DecisionTreeDictionary.PRUNED_TREE);
        options.add(String.valueOf(!getUnpruned()));
        if (!getUnpruned()) {
            options.add(DecisionTreeDictionary.NUM_FOLDS);
            options.add(String.valueOf(getNumFolds()));
        }
        return options;
    }
}
