/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.core.evaluation;

import eca.core.InstancesHandler;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Enumeration;
import java.util.Random;

/**
 * Implements evaluation of the classification results.
 *
 * @author Roman Batygin
 */
public class Evaluation extends weka.classifiers.evaluation.Evaluation implements InstancesHandler {

    private static final int MINIMUM_NUMBER_OF_FOLDS = 2;
    private static final int MINIMUM_NUMBER_OF_TESTS = 1;

    private Long totalTimeMillis;

    private int numTests;

    private int seed;

    private double varianceError;

    private double[] error;

    private final Instances initialData;

    /**
     * Creates evaluation object.
     *
     * @param initialData - instances (training data)
     * @throws Exception in case of error
     */
    public Evaluation(Instances initialData) throws Exception {
        super(initialData);
        this.initialData = initialData;
    }

    /**
     * Returns tests number for k*V cross validation method.
     *
     * @return tests number
     */
    public int getNumTests() {
        return numTests;
    }

    /**
     * Sets tests number.
     *
     * @param numTests - tests number
     */
    public void setNumTests(int numTests) {
        this.numTests = numTests;
    }

    /**
     * Returns the number of folds.
     *
     * @return the number of folds
     */
    public int numFolds() {
        return this.m_NumFolds;
    }

    /**
     * Sets the number of folds.
     *
     * @param m_NumFolds - the number of folds
     */
    public void setFolds(int m_NumFolds) {
        this.m_NumFolds = m_NumFolds;
    }

    /**
     * Returns instances object (training data).
     *
     * @return instances object (training data)
     */
    @Override
    public Instances getData() {
        return initialData;
    }

    /**
     * Returns using for k * V - folds cross - validation method.
     *
     * @return using for k * V - folds cross - validation method
     */
    public boolean isKCrossValidationMethod() {
        return this.m_NumFolds > 1;
    }

    /**
     * Gets seed value for k * V - folds cross - validation method.
     *
     * @return seed value
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Sets seed value for k * V - folds cross - validation method.
     *
     * @param seed - sees value
     */
    public void setSeed(int seed) {
        this.seed = seed;
    }

    /**
     * Evaluates model using k * V - folds cross - validation method.
     *
     * @param classifier - classifier object.
     * @param data       - training data
     * @param numFolds   - the number of folds
     * @param numTests   - the number of tests
     * @param seed       - seed value
     * @throws Exception in case of error
     */
    public void kCrossValidateModel(Classifier classifier, Instances data, int numFolds,
                                    int numTests, int seed) throws Exception {
        if (numFolds < MINIMUM_NUMBER_OF_FOLDS) {
            throw new IllegalArgumentException(
                    String.format("Number of folds must be greater or equals to %d!", numFolds));
        }
        if (numTests < MINIMUM_NUMBER_OF_TESTS) {
            throw new IllegalArgumentException(
                    String.format("Number of tests must be greater or equals to %d!", numTests));
        }
        Random random = new Random(seed);
        error = new double[numTests * numFolds];

        for (int i = 0; i < numTests; i++) {

            Instances current = new Instances(data);
            current.randomize(random);
            current.stratify(numFolds);

            for (int j = 0; j < numFolds; j++) {
                Instances train = current.trainCV(numFolds, j, random);
                setPriors(train);
                Classifier c = AbstractClassifier.makeCopy(classifier);
                c.buildClassifier(train);
                Instances test = current.testCV(numFolds, j);
                evaluateModel(c, test);
                error[i * numFolds + j] = error(c, test);
            }
        }
        this.setFolds(numFolds);
        this.setNumTests(numTests);
        this.setSeed(seed);
        this.computeErrorVariance(error);
    }

    /**
     * Calculates the variance of mean error.
     *
     * @param error errors array
     */
    public void computeErrorVariance(double[] error) {
        this.error = error;
        double meanError = pctIncorrect() / 100.0;
        varianceError = 0.0;
        for (double err : error) {
            varianceError += (err - meanError) * (err - meanError);
        }
        varianceError /= error.length - 1;
    }

    /**
     * Returns the value of Student confidence interval for classifier mean error.
     *
     * @return the value of Student confidence interval for classifier mean error
     */
    public double errorConfidenceValue() {
        return eca.statistics.Statistics.studentConfidenceInterval(error.length, 0.05, stdDeviationError());
    }

    /**
     * Returns the variance of error.
     *
     * @return the variance of error
     */
    public double varianceError() {
        return varianceError;
    }

    /**
     * Returns the standard deviation of error.
     *
     * @return the standard deviation of error
     */
    public double stdDeviationError() {
        return Math.sqrt(varianceError);
    }

    /**
     * Returns the Student confidence interval for classifier mean error.
     *
     * @return the Student confidence interval for classifier mean error
     */
    public double[] errorConfidenceInterval() {
        double x = errorConfidenceValue();
        return new double[] {pctIncorrect() / 100.0 - x, pctIncorrect() / 100.0 + x};
    }

    /**
     * Returns the value of maximum area under ROC - curve.
     *
     * @return the value of maximum area under ROC - curve
     */
    public double maxAreaUnderROC() {
        double maxVal = -Double.MAX_VALUE;
        for (int i = 0; i < getData().numClasses(); i++) {
            maxVal = Double.max(maxVal, areaUnderROC(i));
        }
        return maxVal;
    }

    /**
     * Calculates classifier error on given data.
     *
     * @param classifier classifier object
     * @param data       instances object
     * @return the value of classifier error
     * @throws Exception
     */
    public static double error(Classifier classifier, Instances data) throws Exception {
        int count = 0;
        for (Enumeration<Instance> objects = data.enumerateInstances();
             objects.hasMoreElements(); ) {
            Instance obj = objects.nextElement();
            if (obj.classValue() != classifier.classifyInstance(obj)) {
                count++;
            }
        }
        return (double) count / data.numInstances();
    }

    /**
     * Returns evaluation total time in milliseconds.
     *
     * @return evaluation total time in milliseconds
     */
    public Long getTotalTimeMillis() {
        return totalTimeMillis;
    }

    /**
     * Sets the evaluation total time in milliseconds.
     *
     * @param totalTimeMillis evaluation total time in milliseconds
     */
    public void setTotalTimeMillis(Long totalTimeMillis) {
        this.totalTimeMillis = totalTimeMillis;
    }
}
