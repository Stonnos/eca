/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.ensemble.voting.WeightedVoting;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Implements AdaBoost algorithm. For more information see <p>
 * Yoav Freund, Robert E. Schapire. A decision-theoretic generalization of online learning
 * and an application to boosting // Second European Conference on Computational Learning Theory. – 1995. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set individual classifiers collection  <p>
 * <p>
 * Set minimum error threshold for including classifier in ensemble <p>
 * <p>
 * Set maximum error threshold for including classifier in ensemble <p>
 * <p>
 * Sets {@link Sampler} object. <p>
 *
 * @author Roman Batygin
 */
public class AdaBoostClassifier extends AbstractHeterogeneousClassifier {

    /**
     * Instances weights
     **/
    private double[] weights;

    private final Random random = new Random();

    /**
     * Creates <tt>AdaBoostClassifier</tt> object.
     */
    public AdaBoostClassifier() {

    }

    /**
     * Creates <tt>AdaBoostClassifier</tt> object with given classifiers set.
     *
     * @param set classifiers set
     */
    public AdaBoostClassifier(ClassifiersSet set) {
        super(set);
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new AdaBoostBuilder(data);
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(getClassifiersSet().size() + 3) * 2];
        int k = 0;
        options[k++] = EnsembleDictionary.NUM_ITS;
        options[k++] = String.valueOf(getIterationsNum());
        options[k++] = EnsembleDictionary.MIN_ERROR;
        options[k++] = String.valueOf(getMinError());
        options[k++] = EnsembleDictionary.MAX_ERROR;
        options[k++] = String.valueOf(getMaxError());
        for (int i = k++, j = 0; i < options.length; i += 2, j++) {
            options[i] = String.format(EnsembleDictionary.INDIVIDUAL_CLASSIFIER_FORMAT, j);
            options[i + 1] = getClassifiersSet().getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    private double weightedError(Classifier model) throws Exception {
        double error = 0.0;
        for (int i = 0; i < filteredData.numInstances(); i++) {
            Instance obj = filteredData.instance(i);
            if (obj.classValue() != model.classifyInstance(obj)) {
                error += weights[i];
            }
        }
        return error;
    }

    private void initializeWeights() {
        double w0 = 1.0 / filteredData.numInstances();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = w0;
        }
    }

    private void updateWeights(int t) throws Exception {
        double sumWeights = 0.0;
        WeightedVoting v = (WeightedVoting) votes;
        for (int i = 0; i < weights.length; i++) {
            Instance obj = filteredData.instance(i);
            int sign = obj.classValue() == classifiers.get(t).classifyInstance(obj) ? 1 : -1;
            weights[i] = weights[i] * Math.exp(-v.getWeight(t) * sign);
            sumWeights += weights[i];
        }
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / sumWeights;
        }
    }

    private boolean nextIteration(int t) throws Exception {
        Instances sample = filteredData.resampleWithWeights(random, weights);
        Classifier model = null;
        double minError = Double.MAX_VALUE;
        for (int i = 0; i < getClassifiersSet().size(); i++) {
            Classifier classifier = getClassifiersSet().getClassifierCopy(i);
            classifier.buildClassifier(sample);
            double error = weightedError(classifier);
            if (error < minError) {
                minError = error;
                model = classifier;
            }
        }
        if (minError > getMinError() && minError < getMaxError()) {
            classifiers.add(model);
            ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(minError));
            updateWeights(t);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void initialize() {
        votes = new WeightedVoting(new Aggregator(this), getIterationsNum());
        weights = new double[filteredData.numInstances()];
        initializeWeights();
    }

    /**
     *
     */
    private class AdaBoostBuilder extends AbstractBuilder {

        AdaBoostBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (!nextIteration(index)) {
                step = getIterationsNum() - index;
                index = getIterationsNum() - 1;
            }
            if (index == getIterationsNum() - 1) {
                checkModel();
            }
            return ++index;
        }

    } //End of class AdaBoostBuilder

} //End of class AdaBoostClassifier
