/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Evaluates classifier using k * V - folds cross - validation method. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Sets the number of folds <p>
 * <p>
 * Sets the number of validations <p>
 *
 * @author Roman Batygin
 */
public class CVIterativeBuilder extends IterativeBuilder {

    /**
     * Iterable classifier
     **/
    private final Iterable classifier;

    /**
     * Training set
     **/
    private final Instances data;

    /**
     * Number of folds
     **/
    private final int numFolds;

    /**
     * Number of validations
     **/
    private final int numValidations;

    /**
     * Evaluation object
     **/
    private final Evaluation evaluation;

    private int cvIndex = -1;
    private int validIndex;
    private Instances currentSet;
    private Random random = new Random();
    private double[] error;

    /**
     * Creates <tt>CVIterativeBuilder</tt> object with given options.
     *
     * @param classifier     iterable classifier object.
     * @param data           <tt>Instances</tt> object (training set)
     * @param numFolds       number of folds
     * @param numValidations number of validations
     * @throws Exception
     */
    public CVIterativeBuilder(Iterable classifier,
                              Instances data, int numFolds, int numValidations) throws Exception {
        this.classifier = classifier;
        this.data = new Instances(data);
        this.currentSet = data;
        this.evaluation = new Evaluation(data);
        this.evaluation.setFolds(numFolds);
        this.evaluation.setValidationsNum(numValidations);
        this.numFolds = numFolds;
        this.numValidations = numValidations;
        error = new double[numValidations * numFolds];
    }

    @Override
    public int numIterations() {
        return numFolds * numValidations + 1;
    }

    @Override
    public Evaluation evaluation() {
        return evaluation;
    }

    @Override
    public int next() throws Exception {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        for (; validIndex < numValidations; validIndex++) {

            for (cvIndex++; cvIndex < numFolds; ) {
                if (cvIndex == 0) {
                    currentSet = new Instances(data);
                    currentSet.randomize(random);
                    currentSet.stratify(numFolds);
                }
                Instances train = currentSet.trainCV(numFolds, cvIndex);
                evaluation.setPriors(train);
                Classifier c = AbstractClassifier.makeCopy((Classifier) classifier);
                c.buildClassifier(train);
                Instances test = currentSet.testCV(numFolds, cvIndex);
                evaluation.evaluateModel(c, test);
                error[index] = Evaluation.error(c, test);
                return ++index;
            }
            cvIndex = -1;
        }

        evaluation.computeErrorVariance(error);

        IterativeBuilder i = classifier.getIterativeBuilder(data);
        while (i.hasNext()) {
            i.next();
        }
        return ++index;
    }

    @Override
    public boolean hasNext() {
        return index < numIterations();
    }

}
