/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Implements modified heterogeneous ensemble algorithm.
 *
 * @author Рома
 */
public class ModifiedHeterogeneousClassifier extends HeterogeneousClassifier {

    private SubspacesAggregator aggregator;

    /**
     * Creates <tt>ModifiedHeterogeneousClassifier</tt> with given classifiers set.
     *
     * @param set classifiers set
     */
    public ModifiedHeterogeneousClassifier(ClassifiersSet set) {
        super(set);
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new SpaceBuilder(data);
    }

    @Override
    protected void initialize() {
        aggregator = new SubspacesAggregator(this);

        if (getUseWeightedVotesMethod()) {
            votes = new WeightedVoting(aggregator, numIterations);
        } else {
            votes = new MajorityVoting(aggregator);
        }
    }

    /**
     *
     */
    private class SpaceBuilder extends AbstractBuilder {

        Random random = new Random();

        public SpaceBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances subSample =
                    sampler().instances(filteredData, random.nextInt(filteredData.numAttributes() - 1) + 1);

            Classifier model = getUseRandomClassifier() ? set.buildRandomClassifier(subSample)
                    : set.builtOptimalClassifier(subSample);

            double error = Evaluation.error(model, subSample);

            if (error > min_error && error < max_error) {
                classifiers.add(model);
                aggregator.setInstances(subSample);
                if (getUseWeightedVotesMethod()) {
                    ((WeightedVoting) votes).setWeight(0.5 * Math.log((1.0 - error) / error));
                }
            }
            if (index == numIterations - 1) {
                checkModel();
            }
            return ++index;
        }

    } //End of class SpaceBuilder

}
