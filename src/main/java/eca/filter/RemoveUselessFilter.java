package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.RemoveUseless;

/**
 * @author Roman Batygin
 */

public class RemoveUselessFilter extends MissingValuesFilter {

    private RemoveUseless removeUseless = new RemoveUseless();

    @Override
    public Instance filterInstance(Instance obj) {
        Instance instance = super.filterInstance(obj);
        removeUseless.input(instance);
        return removeUseless.output();
    }

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        Instances train = super.filterInstances(data);
        removeUseless.setInputFormat(train);
        train = weka.filters.Filter.useFilter(train, removeUseless);
        train.setRelationName(data.relationName());
        return train;
    }
}
