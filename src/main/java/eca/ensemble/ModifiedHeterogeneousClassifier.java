/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import java.util.Random;

import eca.core.evaluation.Evaluation;
import weka.core.Instances;
import java.util.NoSuchElementException;
import weka.classifiers.Classifier;

/**
 *
 * @author Рома
 */
public class ModifiedHeterogeneousClassifier extends HeterogeneousClassifier {

    private SubspacesAggregator aggregator;

    public ModifiedHeterogeneousClassifier(ClassifiersSet set) {
        super(set);
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        aggregator = new SubspacesAggregator(this, numIterations);
        votes = (getUseWeightedVotesMethod())
                ? new WeightedVoting(aggregator, numIterations)
                : new MajorityVoting(aggregator);
        return new SpaceBuilder(data);
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
            Instances subSample = sampler().instances(data, random.nextInt(data.numAttributes() - 1) + 1);

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
