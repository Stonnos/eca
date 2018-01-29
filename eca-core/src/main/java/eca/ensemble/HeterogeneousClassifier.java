/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble;

import eca.core.evaluation.Evaluation;
import eca.ensemble.sampling.Sampler;
import eca.ensemble.sampling.SamplingMethod;
import eca.ensemble.voting.MajorityVoting;
import eca.ensemble.voting.WeightedVotesAvailable;
import eca.ensemble.voting.WeightedVoting;
import org.springframework.util.Assert;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.Random;

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
     * Sampling method
     **/
    private SamplingMethod samplingMethod = SamplingMethod.INITIAL;

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
     * Returns sampling method type.
     *
     * @return {@link SamplingMethod} object
     */
    public final SamplingMethod getSamplingMethod() {
        return samplingMethod;
    }

    /**
     * Sets sampling method type.
     * @param samplingMethod {@link SamplingMethod} object
     */
    public void setSamplingMethod(SamplingMethod samplingMethod) {
        Assert.notNull(samplingMethod, "Sampling method is not specified!");
        this.samplingMethod = samplingMethod;
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
        options[k++] = COMMON_DECIMAL_FORMAT.format(getMinError());
        options[k++] = EnsembleDictionary.MAX_ERROR;
        options[k++] = COMMON_DECIMAL_FORMAT.format(getMaxError());
        options[k++] = EnsembleDictionary.VOTING_METHOD;
        options[k++] = getUseWeightedVotesMethod() ?
                EnsembleDictionary.WEIGHTED_VOTING : EnsembleDictionary.MAJORITY_VOTING;
        options[k++] = EnsembleDictionary.SAMPLING_METHOD;
        options[k++] = getSamplingMethod().getDescription();
        options[k++] = EnsembleDictionary.CLASSIFIER_SELECTION;
        options[k++] = getUseRandomClassifier() ? EnsembleDictionary.RANDOM_CLASSIFIER
                : EnsembleDictionary.OPTIMAL_CLASSIFIER;
        for (int j = 0; k < options.length; k += 2, j++) {
            options[k] = String.format(EnsembleDictionary.INDIVIDUAL_CLASSIFIER_FORMAT, j);
            options[k + 1] = getClassifiersSet().getClassifier(j).getClass().getSimpleName();
        }
        return options;
    }

    @Override
    protected void initializeOptions() {
        if (getSamplingMethod() == SamplingMethod.INITIAL) {
            setIterationsNum(getClassifiersSet().size());
        }
        votes = getUseWeightedVotesMethod() ? new WeightedVoting(new Aggregator(this), getIterationsNum()) :
                new MajorityVoting(new Aggregator(this));
    }

    @Override
    protected Instances createSample() throws Exception {
        return Sampler.instances(samplingMethod, filteredData, new Random());
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) throws Exception {
        Classifier model;
        if (getSamplingMethod() == SamplingMethod.INITIAL) {
            model = getClassifiersSet().buildClassifier(iteration, data);
        } else if (getUseRandomClassifier()) {
            model = getClassifiersSet().buildRandomClassifier(data);
        } else {
            model = getClassifiersSet().builtOptimalClassifier(data);
        }
        return model;
    }

    @Override
    protected synchronized void addClassifier(Classifier classifier, Instances data) throws Exception {
        double error = Evaluation.error(classifier, data);
        if (error > getMinError() && error < getMaxError()) {
            classifiers.add(classifier);
            if (getUseWeightedVotesMethod()) {
                ((WeightedVoting) votes).setWeight(EnsembleUtils.getClassifierWeight(error));
            }
        }
    }

}