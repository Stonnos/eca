package eca.ensemble.forests;

import eca.ensemble.EnsembleDictionary;
import eca.ensemble.IterativeBuilder;
import eca.trees.DecisionTreeClassifier;
import eca.trees.DecisionTreeDictionary;
import weka.core.Instances;

import java.util.List;
import java.util.NoSuchElementException;

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

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new ExtraTreesBuilder(data);
    }

    /**
     * Sets the value of use bootstrap samples.
     *
     * @param useBootstrapSamples the value of use bootstrap samples
     */
    public void setUseBootstrapSamples(boolean useBootstrapSamples) {
        this.useBootstrapSamples = useBootstrapSamples;
    }

    /**
     * Extra trees iterative builder.
     */
    private class ExtraTreesBuilder extends ForestBuilder {

        ExtraTreesBuilder(Instances dataSet) throws Exception {
            super(dataSet);
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances sample;
            if (isUseBootstrapSamples()) {
                sample = sampler.bootstrap(filteredData);
            } else {
                sample = sampler.initial(filteredData);
            }
            DecisionTreeClassifier treeClassifier = createDecisionTree();
            treeClassifier.setUseRandomSplits(true);
            treeClassifier.setNumRandomSplits(getNumRandomSplits());
            treeClassifier.buildClassifier(sample);
            classifiers.add(treeClassifier);
            return ++index;
        }
    }
}
