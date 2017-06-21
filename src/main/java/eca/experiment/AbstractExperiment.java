/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.experiment;

import java.util.ArrayList;
import eca.beans.ClassifierDescriptor;
import eca.core.TestMethod;
import java.util.Random;

import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;
import weka.classifiers.Classifier;

/**
 * 
 * @author Roman93
 * @param <T> 
 */
public abstract class AbstractExperiment<T extends Classifier>
        implements Experiment<T>, IterativeableExperiment {

    private final ArrayList<ClassifierDescriptor> experiment;
    private int numFolds = 10;
    private int numValidations = 10;
    private int numIterations = 100;
    private int mode = TestMethod.TRAINING_SET;
    private final Instances data;
    protected T classifier;
    protected final Random r = new Random();

    protected AbstractExperiment(Instances data, T classifier) {
        this(data, classifier, 16);
    }

    protected AbstractExperiment(Instances data, T classifier, int size) {
        this.data = data;
        this.classifier = classifier;
        experiment = new ArrayList<>(size);
    }

    @Override
    public void clearHistory() {
        getHistory().clear();
    }
    
    @Override
    public T getClassifier() {
        return classifier;
    }

    @Override
    public final int getNumIterations() {
        return numIterations;
    }

    @Override
    public final void setNumIterations(int numIterations) {
        if (numIterations <= 0) {
            throw new IllegalArgumentException("Число экспериментов должно быть больше нуля!");
        }
        this.numIterations = numIterations;
    }

    public int getNumFolds() {
        return numFolds;
    }

    public int getTestMethod() {
        return mode;
    }

    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    public int getNumValidations() {
        return numValidations;
    }

    public void setNumValidations(int numValidations) {
        this.numValidations = numValidations;
    }

    public void setTestMethod(int mode) {
        if (mode != TestMethod.TRAINING_SET && mode != TestMethod.CROSS_VALIDATION) {
            throw new IllegalArgumentException("Wrong mode value!");
        }
        this.mode = mode;
    }

    @Override
    public Instances data() {
        return data;
    }

    @Override
    public ArrayList<ClassifierDescriptor> getHistory() {
        return experiment;
    }

    protected final ClassifierDescriptor evaluateModel(Classifier model) throws Exception {
        Evaluation ev = new Evaluation(data());

        switch (getTestMethod()) {
            case TestMethod.TRAINING_SET:
                model.buildClassifier(data());
                ev.evaluateModel(model, data());
                break;
            case TestMethod.CROSS_VALIDATION:
                ev.kCrossValidateModel(AbstractClassifier.makeCopy(model), data(), getNumFolds(), getNumValidations(), r);
                model.buildClassifier(data());
                break;
        }

        ClassifierDescriptor object = new ClassifierDescriptor(model, ev);
        getHistory().add(object);

        return object;
    }

    @Override
    public void beginExperiment() throws Exception {
        IterativeExperiment iterativeExperiment = getIterativeExperiment();
        while (iterativeExperiment.hasNext()) {
            iterativeExperiment.next();
        }
    }

}
