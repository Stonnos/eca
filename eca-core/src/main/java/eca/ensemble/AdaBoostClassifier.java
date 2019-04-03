/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.ensemble.sampling.Sampler;
import eca.ensemble.voting.WeightedVoting;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Randomizable;
import weka.core.Utils;

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

    /**
     * Random instance for resampling
     */
    private Random sampleRandom;

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
    public Integer getNumThreads() {
        return null;
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new AdaBoostBuilder(data);
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(getClassifiersSet().size() + 4) * 2];
        int k = 0;
        options[k++] = EnsembleDictionary.NUM_ITS;
        options[k++] = String.valueOf(getNumIterations());
        options[k++] = EnsembleDictionary.MIN_ERROR;
        options[k++] = COMMON_DECIMAL_FORMAT.format(getMinError());
        options[k++] = EnsembleDictionary.MAX_ERROR;
        options[k++] = COMMON_DECIMAL_FORMAT.format(getMaxError());
        options[k++] = EnsembleDictionary.SEED;
        options[k++] = String.valueOf(getSeed());
        for (int j = 0; k < options.length; k += 2, j++) {
            options[k] = String.format(EnsembleDictionary.INDIVIDUAL_CLASSIFIER_FORMAT, j);
            options[k + 1] = getClassifiersSet().getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    private void initializeWeights() {
        double w0 = 1.0 / filteredData.numInstances();
        for (int i = 0; i < weights.length; i++) {
            weights[i] = w0;
        }
    }

    @Override
    protected void initializeOptions() {
        sampleRandom = new Random(getSeed());
        votes = new WeightedVoting(new Aggregator(classifiers, filteredData), getNumIterations());
        weights = new double[filteredData.numInstances()];
        initializeWeights();
    }

    @Override
    protected Instances createSample(int iteration) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void addClassifier(Classifier classifier, Instances data) {
        throw new UnsupportedOperationException();
    }

    /**
     * AdaBoost iterative builder.
     */
    private class AdaBoostBuilder extends IterativeEnsembleBuilder {

        AdaBoostBuilder(Instances data) throws Exception {
            super(data);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (!nextIteration(index)) {
                index = getNumIterations() - 1;
            }
            if (index == getNumIterations() - 1) {
                checkModelForEmpty();
            }
            return ++index;
        }

        boolean nextIteration(int t) throws Exception {
            Instances sample = filteredData.resampleWithWeights(sampleRandom, weights);
            Classifier model = null;
            double minError = Double.MAX_VALUE;
            for (int i = 0; i < getClassifiersSet().size(); i++) {
                Classifier classifier = getClassifiersSet().getClassifierCopy(i);
                if (classifier instanceof Randomizable) {
                    ((Randomizable) classifier).setSeed(seeds[t]);
                }
                classifier.buildClassifier(sample);
                double error = weightedError(classifier);
                if (error < minError) {
                    minError = error;
                    model = classifier;
                }
            }
            if (minError > getMinError() && minError < getMaxError()) {
                classifiers.add(model);
                ((WeightedVoting) votes).addWeight(EnsembleUtils.getClassifierWeight(minError));
                updateWeights(t);
                return true;
            } else {
                return false;
            }
        }

        void updateWeights(int t) throws Exception {
            double sumWeights = 0.0;
            WeightedVoting v = (WeightedVoting) votes;
            for (int i = 0; i < weights.length; i++) {
                Instance obj = filteredData.instance(i);
                int sign = obj.classValue() == classifiers.get(t).classifyInstance(obj) ? 1 : -1;
                weights[i] = weights[i] * Math.exp(-v.getWeight(t) * sign);
                sumWeights += weights[i];
            }
            Utils.normalize(weights, sumWeights);
        }

        double weightedError(Classifier model) throws Exception {
            double error = 0.0;
            for (int i = 0; i < filteredData.numInstances(); i++) {
                Instance obj = filteredData.instance(i);
                if (obj.classValue() != model.classifyInstance(obj)) {
                    error += weights[i];
                }
            }
            return error;
        }

    } //End of class AdaBoostBuilder

} //End of class AdaBoostClassifier
