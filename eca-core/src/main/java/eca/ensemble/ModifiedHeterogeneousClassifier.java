/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

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
        SubspacesAggregator aggregator = new SubspacesAggregator(classifiers, filteredData);
        votes = getUseWeightedVotes() ? new WeightedVoting(aggregator) : new MajorityVoting(aggregator);
    }

    @Override
    protected Instances createSample(int iteration) {
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

}
