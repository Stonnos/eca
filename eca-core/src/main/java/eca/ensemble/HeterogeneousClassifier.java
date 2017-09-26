/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.ensemble.voting.MajorityVoting;
import eca.ensemble.voting.WeightedVotesAvailable;
import eca.ensemble.voting.WeightedVoting;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.NoSuchElementException;

/**
 * Implements heterogeneous ensemble algorithm. <p>
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
public class HeterogeneousClassifier extends AbstractHeterogeneousClassifier
        implements WeightedVotesAvailable {

    /**
     * Use weighted votes method?
     **/
    private boolean useWeightedVotes;

    /**
     * Use randomly classifiers selection?
     **/
    private boolean useRandomClassifier = true;

    /**
     * Sampling object
     **/
    private final Sampler sampler = new Sampler();

    /**
     * Creates <tt>HeterogeneousClassifier</tt> object.
     */
    public HeterogeneousClassifier() {

    }

    /**
     * Creates <tt>HeterogeneousClassifier</tt> object with given classifiers set.
     *
     * @param set classifiers set
     */
    public HeterogeneousClassifier(ClassifiersSet set) {
        super(set);
    }

    /**
     * Returns <tt>Sampler</tt> object.
     *
     * @return <tt>Sampler</tt> object
     */
    public final Sampler sampler() {
        return sampler;
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new HeterogeneousBuilder(data);
    }

    @Override
    public boolean getUseWeightedVotesMethod() {
        return useWeightedVotes;
    }

    @Override
    public void setUseWeightedVotesMethod(boolean flag) {
        this.useWeightedVotes = flag;
    }

    /**
     * Returns the value of use random classifier.
     *
     * @return the value of use random classifier
     */
    public boolean getUseRandomClassifier() {
        return useRandomClassifier;
    }

    /**
     * Sets the value of use random classifier.
     *
     * @param flag the value of use random classifier
     */
    public void setUseRandomClassifier(boolean flag) {
        this.useRandomClassifier = flag;
    }

    @Override
    public String[] getOptions() {
        String[] options = new String[(getClassifiersSet().size() + 6) * 2];
        int k = 0;
        options[k++] = EnsembleDictionary.NUM_ITS;
        options[k++] = String.valueOf(getIterationsNum());
        options[k++] = EnsembleDictionary.MIN_ERROR;
        options[k++] = String.valueOf(getMinError());
        options[k++] = EnsembleDictionary.MAX_ERROR;
        options[k++] = String.valueOf(getMaxError());
        options[k++] = EnsembleDictionary.VOTING_METHOD;
        options[k++] = getUseWeightedVotesMethod() ?
                EnsembleDictionary.WEIGHTED_VOTING : EnsembleDictionary.MAJORITY_VOTING;
        options[k++] = EnsembleDictionary.SAMPLING_METHOD;
        options[k++] = sampler.getDescription();
        options[k++] = EnsembleDictionary.CLASSIFIER_SELECTION;
        options[k++] = getUseRandomClassifier() ? EnsembleDictionary.RANDOM_CLASSIFIER
                : EnsembleDictionary.OPTIMAL_CLASSIFIER;
        for (int i = k++, j = 0; i < options.length; i += 2, j++) {
            options[i] = String.format(EnsembleDictionary.INDIVIDUAL_CLASSIFIER_FORMAT, j);
            options[i + 1] = getClassifiersSet().getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    @Override
    protected void initialize() {
        if (sampler.getSamplingMethod() == SamplingMethod.INITIAL) {
            setIterationsNum(getClassifiersSet().size());
        }

        if (getUseWeightedVotesMethod()) {
            votes = new WeightedVoting(new Aggregator(this), getIterationsNum());
        } else {
            votes = new MajorityVoting(new Aggregator(this));
        }
    }

    /**
     *
     */
    private class HeterogeneousBuilder extends AbstractBuilder {

        HeterogeneousBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Instances bag = sampler.instances(filteredData);
            Classifier model;
            if (sampler.getSamplingMethod() == SamplingMethod.INITIAL) {
                model = getClassifiersSet().buildClassifier(index, bag);
            } else if (getUseRandomClassifier()) {
                model = getClassifiersSet().buildRandomClassifier(bag);
            } else {
                model = getClassifiersSet().builtOptimalClassifier(bag);
            }

            double error = Evaluation.error(model, bag);

            if (error > getMinError() && error < getMaxError()) {
                classifiers.add(model);
                if (getUseWeightedVotesMethod()) {
                    ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
                }
            }
            if (index == getIterationsNum() - 1) {
                checkModel();
            }
            return ++index;
        }

    } //End of class HeterogeneousBuilder

} //End of class HeterogeneousClassifier
