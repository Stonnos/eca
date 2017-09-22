/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.EvaluationMethod;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Random;

/**
 * Abstract class for automatic selection of optimal options
 * for classifiers based on experiment series.
 * <p>
 * Valid options are: <p>
 * <p>
 * Sets the number of folds (Default: 10) <p>
 * <p>
 * Sets the number of validations (Default: 10) <p>
 * <p>
 * Sets the number of iterations (Default: 100) <p>
 * <p>
 * Sets evaluation method (Default: TRAINING_DATA) <p>
 *
 * @param <T> classifier type
 * @author Roman Batygin
 */
public abstract class AbstractExperiment<T extends Classifier>
        implements Experiment<T>, IterableExperiment {

    /**
     * Experiment history
     **/
    private final ArrayList<EvaluationResults> experiment;

    /**
     * Number of folds
     **/
    private int numFolds = 10;

    /**
     * Number of tests
     **/
    private int numTests = 10;

    /**
     * Number of iterations
     **/
    private int numIterations = 100;

    /**
     * Evaluation method
     **/
    private EvaluationMethod evaluationMethod = EvaluationMethod.TRAINING_DATA;

    /**
     * Training set
     **/
    private final Instances data;

    /**
     * Classifier object
     **/
    protected T classifier;

    protected final Random r = new Random();

    protected AbstractExperiment(Instances data, T classifier) {
        this(data, classifier, 100);
    }

    protected AbstractExperiment(Instances data, T classifier, int size) {
        this.data = data;
        this.classifier = classifier;
        this.experiment = new ArrayList<>(size);
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
     *
     * @return the number of folds
     */
    public int getNumFolds() {
        return numFolds;
    }

    /**
     * Return the evaluation method type.
     *
     * @return the evaluation method type
     */
    public EvaluationMethod getEvaluationMethod() {
        return evaluationMethod;
    }

    /**
     * Sets the number of folds.
     *
     * @param numFolds the number of folds
     */
    public void setNumFolds(int numFolds) {
        this.numFolds = numFolds;
    }

    /**
     * Returns the number of tests.
     *
     * @return the number of tests
     */
    public int getNumTests() {
        return numTests;
    }

    /**
     * Sets the number of tests.
     *
     * @param numTests the number of tests
     */
    public void setNumTests(int numTests) {
        this.numTests = numTests;
    }

    /**
     * Sets the evaluation method type
     *
     * @param evaluationMethod evaluation method type
     */
    public void setEvaluationMethod(EvaluationMethod evaluationMethod) {
        this.evaluationMethod = evaluationMethod;
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public ArrayList<EvaluationResults> getHistory() {
        return experiment;
    }

    protected final EvaluationResults evaluateModel(Classifier model) throws Exception {

        Evaluation evaluation = EvaluationService.evaluateModel(model, getData(),
                getEvaluationMethod(), getNumFolds(), getNumTests(), r);

        EvaluationResults object = new EvaluationResults(model, evaluation);
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
