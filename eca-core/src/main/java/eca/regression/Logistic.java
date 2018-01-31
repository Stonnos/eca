/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.regression;

import eca.core.InstancesHandler;
import weka.core.Instances;

/**
 * Class for generating logistic regression model.
 *
 * @author Roman Batygin
 */
public class Logistic extends weka.classifiers.functions.Logistic
        implements InstancesHandler {

    private static final int DEFAULT_NUM_ITS = 200;

    private Instances data;

    public Logistic() {
        this.setMaxIts(DEFAULT_NUM_ITS);
    }

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
        return new String[] {LogisticDictionary.MAX_ITS_NUM, String.valueOf(getMaxIts()),
                LogisticDictionary.OPTIMIZATION_METHOD, getUseConjugateGradientDescent()
                ? LogisticDictionary.CONJUGATE_GRADIENT_DESCENT_METHOD : LogisticDictionary.NEWTON_METHOD};
    }
}
