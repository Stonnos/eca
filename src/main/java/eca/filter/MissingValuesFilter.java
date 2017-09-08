/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.filter;

import weka.core.Instance;
import weka.core.Instances;
import weka.filters.unsupervised.attribute.ReplaceMissingValues;

/**
 * Implements filtering missing values for input data.
 */
public class MissingValuesFilter implements Filter, java.io.Serializable {

    private ReplaceMissingValues missFilter = new ReplaceMissingValues();

    @Override
    public Instance filterInstance(Instance obj) {
        missFilter.input(obj);
        return missFilter.output();
    }

    @Override
    public Instances filterInstances(Instances data) throws Exception {
        if (data == null) {
            throw new NullPointerException();
        }
        if (data.checkForStringAttributes()) {
            throw new Exception("Алгоритм не работает со строковыми атрибутами!");
        }
        if (data.classIndex() == -1) {
            throw new Exception("Выберите атрибут класса!");
        }
        if (data.classAttribute().isNumeric()) {
            throw new Exception("Атрибут класса должен иметь категориальный тип!");
        }
        if (data.classAttribute().numValues() < 2) {
            throw new Exception("Атрибут класса должен иметь не менее двух значений!");
        }
        Instances train = new Instances(data);
        train.deleteWithMissingClass();
        if (train.isEmpty()) {
            throw new Exception("Обучающее множество не содержит объектов с заданными классами!");
        }
        missFilter.setInputFormat(train);
        train = weka.filters.Filter.useFilter(train, missFilter);
        train.setRelationName(data.relationName());
        return train;
    }

} //End of class MissingValuesFilter
