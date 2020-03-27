package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.filters.unsupervised.attribute.RemoveUseless;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.io.Serializable;

/**
 * Implements training data filter for logistic regression classifier.
 *
 * @author Roman Batygin
 */
public class LogisticFilter implements Filter, Serializable {

    private final NominalToBinary ntbFilter = new NominalToBinary();
    private final ReplaceMissingValues missValFilter = new ReplaceMissingValues();
    private final RemoveUseless uselessFilter = new RemoveUseless();

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        missValFilter.setInputFormat(data);
        Instances filtered = weka.filters.Filter.useFilter(data, missValFilter);
        uselessFilter.setInputFormat(filtered);
        filtered = weka.filters.Filter.useFilter(filtered, uselessFilter);
        ntbFilter.setInputFormat(filtered);
        return weka.filters.Filter.useFilter(filtered, ntbFilter);
    }

    @Override
    public Instance filterInstance(Instance obj) throws Exception {
        missValFilter.input(obj);
        Instance filtered = missValFilter.output();
        uselessFilter.input(filtered);
        filtered = uselessFilter.output();
        ntbFilter.input(filtered);
        return ntbFilter.output();
    }
}
