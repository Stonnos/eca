/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.dataminer;

import eca.core.Assert;
import eca.core.evaluation.Evaluation;
import eca.core.evaluation.EvaluationMethod;
import eca.core.evaluation.EvaluationResults;
import eca.core.evaluation.EvaluationService;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.Randomizable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
        implements Experiment<T>, IterableExperiment, Randomizable, Serializable {

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
    private int numTests = 1;

    /**
     * Number of iterations
     **/
    private int numIterations = 100;

    /**
     * Seed value for random generator
     */
    private int seed;

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
    private final T classifier;

    protected Random random = new Random();

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
    public int getNumIterations() {
        return numIterations;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
    }

    @Override
    public void setNumIterations(int numIterations) {
        Assert.greaterThanZero(numIterations, ExperimentDictionary.INVALID_NUM_EXPERIMENTS_ERROR_TEXT);
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
    public List<EvaluationResults> getHistory() {
        return experiment;
    }

    /**
     * Sorts experiment history by best results.
     */
    public void sortByBestResults() {
        experiment.sort(new ClassifierComparator());
    }

    /**
     * Reduces experiment history to specified size.
     *
     * @param size - results size
     */
    public void reduce(int size) {
        List<EvaluationResults> evaluationResults = experiment.stream()
                .limit(size)
                .collect(Collectors.toList());
        clearHistory();
        experiment.ensureCapacity(size);
        experiment.addAll(evaluationResults);
    }

    protected final EvaluationResults evaluateModel(Classifier model) throws Exception {
        Evaluation evaluation = EvaluationService.evaluateModel(model, getData(),
                getEvaluationMethod(), getNumFolds(), getNumTests(), new Random(seed));
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

    /**
     * Base class for experiment iterative building.
     */
    protected abstract class AbstractIterativeBuilder implements IterativeExperiment {

        private int index;

        /**
         * Creates experiment iterative builder.
         */
        AbstractIterativeBuilder() {
            clearHistory();
        }

        /**
         * Increase experiment index.
         */
        void incrementIndex() {
            ++index;
        }

        @Override
        public boolean hasNext() {
            return index < getNumIterations();
        }

        @Override
        public int getPercent() {
            return index * 100 / getNumIterations();
        }

    }

}
