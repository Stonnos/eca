/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.ensemble.sampling.Sampler;
import eca.ensemble.voting.MajorityVoting;
import eca.ensemble.voting.WeightedVoting;
import weka.classifiers.Classifier;
import weka.core.Instances;

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
    protected void initializeOptions() {
        aggregator = new SubspacesAggregator(this);
        votes = getUseWeightedVotes() ? new WeightedVoting(aggregator, getNumIterations()) :
                new MajorityVoting(aggregator);
    }

    @Override
    protected Instances createSample(int iteration) throws Exception {
        Random random = new Random(getSeed() + iteration);
        return Sampler.instances(getSamplingMethod(), filteredData,
                random.nextInt(filteredData.numAttributes() - 1) + 1, random);
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) throws Exception {
        if (getUseRandomClassifier()) {
            return ClassifierBuilder.buildRandomClassifier(getClassifiersSet(), data, new Random(seeds[iteration]),
                    seeds[iteration]);
        } else {
            return ClassifierBuilder.builtOptimalClassifier(getClassifiersSet(), data, seeds[iteration]);
        }
    }

    @Override
    protected synchronized void addClassifier(Classifier classifier, Instances data) throws Exception {
        double error = Evaluation.error(classifier, data);
        if (error > getMinError() && error < getMaxError()) {
            classifiers.add(classifier);
            aggregator.setInstances(data);
            if (getUseWeightedVotes()) {
                ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
            }
        }
    }

}
