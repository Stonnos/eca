package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.RemoveUseless;

import java.io.Serializable;

/**
 * This filter removes attributes that do not vary at all or that vary too much.
 * All constant attributes are deleted automatically, along with any that exceed
 * the maximum percentage of variance parameter.
 *
 * @author Roman Batygin
 */
public class ConstantAttributesFilter implements Filter, Serializable {

    private final RemoveUseless removeUseless = new RemoveUseless();

    @Override
    public Instance filterInstance(Instance obj) {
        removeUseless.input(obj);
        return removeUseless.output();
    }

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        removeUseless.setInputFormat(data);
        Instances train = weka.filters.Filter.useFilter(data, removeUseless);
        train.setRelationName(data.relationName());
        return train;
    }

}
