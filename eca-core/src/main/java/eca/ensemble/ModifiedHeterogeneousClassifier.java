/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.ensemble.voting.MajorityVoting;
import eca.ensemble.voting.WeightedVoting;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Implements modified heterogeneous ensemble algorithm.
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Use weighted votes method. (Default: <tt>false</tt>) <p>
 * <p>
 * Use randomly classifiers selection. (Default: <tt>true</tt>) <p>
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
public class ModifiedHeterogeneousClassifier extends HeterogeneousClassifier {

    private SubspacesAggregator aggregator;

    /**
     * Creates <tt>ModifiedHeterogeneousClassifier</tt> object.
     */
    public ModifiedHeterogeneousClassifier() {

    }

    /**
     * Creates <tt>ModifiedHeterogeneousClassifier</tt> object with given classifiers set.
     *
     * @param set classifiers set
     */
    public ModifiedHeterogeneousClassifier(ClassifiersSet set) {
        super(set);
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new ModifiedHeterogeneousBuilder(data);
    }

    @Override
    protected void initialize() {
        aggregator = new SubspacesAggregator(this);

        if (getUseWeightedVotesMethod()) {
            votes = new WeightedVoting(aggregator, getIterationsNum());
        } else {
            votes = new MajorityVoting(aggregator);
        }
    }

    /**
     * Modified heterogeneous classifier iterative builder.
     */
    private class ModifiedHeterogeneousBuilder extends AbstractBuilder {

        Random random = new Random();

        ModifiedHeterogeneousBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances subSample =
                    sampler().instances(filteredData, random.nextInt(filteredData.numAttributes() - 1) + 1);

            Classifier model;
            if (getUseRandomClassifier()) {
                model = getClassifiersSet().buildRandomClassifier(subSample);
            } else {
                model = getClassifiersSet().builtOptimalClassifier(subSample);
            }

            double error = Evaluation.error(model, subSample);

            if (error > getMinError() && error < getMaxError()) {
                classifiers.add(model);
                aggregator.setInstances(subSample);
                if (getUseWeightedVotesMethod()) {
                    ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
                }
            }
            if (index == getIterationsNum() - 1) {
                checkModel();
            }
            return ++index;
        }

    }

}
