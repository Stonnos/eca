package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.RemoveUseless;

/**
 * Implements filtering of constant attributes in input data.
 *
 * @author Roman Batygin
 */

public class ConstantAttributesFilter extends MissingValuesFilter {

    private final RemoveUseless removeUseless = new RemoveUseless();

    @Override
    public Instance filterInstance(Instance obj) {
        Instance filtered = super.filterInstance(obj);
        removeUseless.input(filtered);
        return removeUseless.output();
    }

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        Instances filtered = super.filterInstances(data);
        removeUseless.setInputFormat(filtered);
        Instances train = weka.filters.Filter.useFilter(filtered, removeUseless);
        train.setRelationName(data.relationName());
        return train;
    }

}
