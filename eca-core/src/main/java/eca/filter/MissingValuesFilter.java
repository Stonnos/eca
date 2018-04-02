/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

import java.util.Objects;

/**
 * Implements filtering of missing values for input data.
 */
public class MissingValuesFilter implements Filter, java.io.Serializable {

    private static final int MIN_NUM_CLASS_VALUES = 2;

    private ReplaceMissingValues missFilter = new ReplaceMissingValues();

    @Override
    public Instance filterInstance(Instance obj) {
        missFilter.input(obj);
        return missFilter.output();
    }

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        Objects.requireNonNull(data, "Input data is not specified!");
        if (data.checkForStringAttributes()) {
            throw new IllegalArgumentException(FilterDictionary.STRING_ATTR_ERROR_TEXT);
        }
        if (data.classIndex() == -1) {
            throw new IllegalArgumentException(FilterDictionary.CLASS_NOT_SELECTED_ERROR_TEXT);
        }
        if (data.classAttribute().isNumeric()) {
            throw new IllegalArgumentException(FilterDictionary.BAD_CLASS_TYPE_ERROR_TEXT);
        }
        if (data.classAttribute().numValues() < MIN_NUM_CLASS_VALUES) {
            throw new IllegalArgumentException(FilterDictionary.BAD_NUMBER_OF_CLASSES_ERROR_TEXT);
        }
        Instances train = new Instances(data);
        train.deleteWithMissingClass();
        if (train.isEmpty()) {
            throw new IllegalArgumentException(FilterDictionary.EMPTY_INSTANCES_ERROR_TEXT);
        }
        missFilter.setInputFormat(train);
        train = weka.filters.Filter.useFilter(train, missFilter);
        train.setRelationName(data.relationName());
        return train;
    }

}
