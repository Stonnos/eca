/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.beans.ClassifierDescriptor;
import eca.core.TestMethod;
import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Random;

/**
 * Abstract class for automatic selection of optimal options
 * for classifiers based on experiment series.
 *
 * Valid options are: <p>
 *
 * Sets the number of folds (Default: 10) <p>
 *
 * Sets the number of validations (Default: 10) <p>
 *
 * Sets the number of iterations (Default: 100) <p>
 *
 * Sets evaluation test method (Default: {@value TestMethod#TRAINING_SET}) <p>
 *
 * @author Roman93
 * @param <T> classifier type
 */
public abstract class AbstractExperiment<T extends Classifier>
        implements Experiment<T>, IterableExperiment {

    /** Experiment history **/
    private final ArrayList<ClassifierDescriptor> experiment;

    /** Number of folds **/
    private int numFolds = 10;

    /** Number of validations **/
    private int numValidations = 10;

    /** Number of iterations **/
    private int numIterations = 100;

    /** Evaluation test method **/
    private int mode = TestMethod.TRAINING_SET;

    /** Training set **/
    private final Instances data;

    /** Classifier object **/
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

    /**
     * Returns the number of folds.
     * @return the number of folds
     */
    public int getNumFolds() {
        return numFolds;
    }

    /**
     * Return the evaluation method type.
     * @return the evaluation method type
     */
    public int getTestMethod() {
        return mode;
    }

    /**
     * Sets the number of folds.
     * @param numFolds the number of folds
     */
    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    /**
     * Returns the number of validations.
     * @return the number of validations
     */
    public int getNumValidations() {
        return numValidations;
    }

    /**
     * Sets the number of validations.
     * @param numValidations the number of validations
     */
    public void setNumValidations(int numValidations) {
        this.numValidations = numValidations;
    }

    /**
     * Sets the evaluation method type
     * @param mode the evaluation method type
     * @exception IllegalArgumentException if the specified method type
     * is invalid
     */
    public void setTestMethod(int mode) {
        if (mode != TestMethod.TRAINING_SET && mode != TestMethod.CROSS_VALIDATION) {
            throw new IllegalArgumentException("Invalid test method value!");
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
