/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.core.Instances;
import java.util.NoSuchElementException;
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import java.util.Random;

/**
 *
 * @author Рома
 */
public class CVIterativeBuilder extends IterativeBuilder {

    private final Iterativeable classifier;
    private final Instances data;
    private final int numFolds;
    private final int numValidations;
    private final Evaluation evaluation;

    private int cvIndex = -1;
    private int validIndex;
    private Instances currentSet;
    private Random r = new Random();
    private double[] error;

    public CVIterativeBuilder(Iterativeable classifier,
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

            for (cvIndex++; cvIndex < numFolds;) {
                if (cvIndex == 0) {
                    currentSet = new Instances(data);
                    currentSet.randomize(r);
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
