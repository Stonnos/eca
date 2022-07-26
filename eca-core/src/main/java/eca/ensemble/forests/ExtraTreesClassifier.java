package eca.ensemble.forests;

import eca.ensemble.EnsembleDictionary;
import eca.ensemble.sampling.Sampler;
import eca.trees.DecisionTreeClassifier;
import eca.trees.DecisionTreeDictionary;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.List;
import java.util.Random;

/**
 * Class for generating Extra trees model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set minimum number of instances per leaf. (Default: 2) <p>
 * <p>
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 * <p>
 * Set the number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 * <p>
 * Set the number of attribute random splits. (Default: 1) <p>
 * <p>
 * Set use bootstrap sample at each iteration. (Default: <tt>false</tt>) <p>
 *
 * @author Roman Batygin
 */
public class ExtraTreesClassifier extends RandomForests {

    private int numRandomSplits = 1;

    private boolean useBootstrapSamples;

    /**
     * Creates <tt>ExtraTreesClassifier</tt> object.
     */
    public ExtraTreesClassifier() {

    }

    /**
     * Creates <tt>ExtraTreesClassifier</tt> object with K / 3 random attributes
     * at each split, where K is the number of input attributes.
     *
     * @param data <tt>Instances</tt> object
     */
    public ExtraTreesClassifier(Instances data) {
        super(data);
    }

    /**
     * Return the value of use bootstrap samples.
     *
     * @return the value of use bootstrap samples
     */
    public boolean isUseBootstrapSamples() {
        return useBootstrapSamples;
    }

    /**
     * Returns the number of random splits of each attribute.
     *
     * @return the number of random splits of each attribute
     */
    public int getNumRandomSplits() {
        return numRandomSplits;
    }

    /**
     * Sets the number of random splits of each attribute.
     *
     * @param numRandomSplits the number of random splits of each attribute
     * @throws IllegalArgumentException if the value the number of random splits is less than
     *                                  specified constant
     */
    public void setNumRandomSplits(int numRandomSplits) {
        if (numRandomSplits < DecisionTreeClassifier.MIN_RANDOM_SPLITS) {
            throw new IllegalArgumentException(
                    String.format(ForestsDictionary.INVALID_NUM_RANDOM_SPLITS_ERROR_MESSAGE_FORMAT,
                            DecisionTreeClassifier.MIN_RANDOM_SPLITS));
        }
        this.numRandomSplits = numRandomSplits;
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = super.getListOptions();
        options.add(DecisionTreeDictionary.NUM_RANDOM_SPLITS);
        options.add(String.valueOf(numRandomSplits));
        options.add(EnsembleDictionary.SAMPLING_METHOD);
        options.add(isUseBootstrapSamples() ? EnsembleDictionary.BOOTSTRAP_SAMPLE_METHOD
                : EnsembleDictionary.TRAINING_SAMPLE_METHOD);
        return options;
    }

    /**
     * Sets the value of use bootstrap samples.
     *
     * @param useBootstrapSamples the value of use bootstrap samples
     */
    public void setUseBootstrapSamples(boolean useBootstrapSamples) {
        this.useBootstrapSamples = useBootstrapSamples;
    }

    @Override
    protected Instances createSample(int iteration) {
        return isUseBootstrapSamples() ? Sampler.bootstrap(filteredData, new Random((long) getSeed() + iteration)) :
                Sampler.initial(filteredData);
    }

    @Override
    protected Classifier buildNextClassifier(int iteration, Instances data) throws Exception {
        DecisionTreeClassifier treeClassifier = createDecisionTree(iteration);
        treeClassifier.getFilter().setDisabled(true);
        treeClassifier.setUseRandomSplits(true);
        treeClassifier.setNumRandomSplits(getNumRandomSplits());
        treeClassifier.buildClassifier(data);
        return treeClassifier;
    }

}
