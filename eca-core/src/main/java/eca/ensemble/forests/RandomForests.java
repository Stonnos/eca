/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.ensemble.forests;

import eca.core.ListOptionsHandler;
import eca.ensemble.Aggregator;
import eca.ensemble.IterativeBuilder;
import eca.ensemble.IterativeEnsembleClassifier;
import eca.ensemble.sampling.Sampler;
import eca.ensemble.voting.MajorityVoting;
import eca.trees.DecisionTreeClassifier;
import eca.trees.DecisionTreeDictionary;
import org.springframework.util.Assert;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class for generating Random forests model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set the number of iterations (Default: 10) <p>
 * <p>
 * Set minimum number of instances per leaf. (Default: 2) <p>
 * <p>
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 * <p>
 * Set number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 *
 * @author Roman Batygin
 */
public class RandomForests extends IterativeEnsembleClassifier implements ListOptionsHandler {

    /**
     * Number of random attributes at each split
     **/
    private int numRandomAttr;

    /**
     * Number of instances per leaf
     **/
    private int minObj = 2;

    /**
     * Maximum tree depth
     **/
    private int maxDepth;

    /**
     * Decision tree type
     */
    private DecisionTreeType decisionTreeType = DecisionTreeType.CART;

    private final DecisionTreeBuilder decisionTreeBuilder = new DecisionTreeBuilder();

    /**
     * Creates <tt>RandomForests</tt> object with K / 3 random attributes
     * at each split, where K is the number of input attributes.
     *
     * @param data <tt>Instances</tt> object
     */
    public RandomForests(Instances data) {
        this.numRandomAttr = (int) Math.sqrt(data.numAttributes());
    }

    /**
     * Creates <tt>RandomForests</tt> object
     */
    public RandomForests() {
    }

    /**
     * Sets the value of random attributes number
     *
     * @param numRandomAttr the value of random attributes number
     * @throws IllegalArgumentException if the value of random attributes number is less than zero
     */
    public void setNumRandomAttr(int numRandomAttr) {
        eca.core.Assert.notNegative(numRandomAttr);
        this.numRandomAttr = numRandomAttr;
    }

    /**
     * Returns the value of random attributes number.
     *
     * @return the value of random attributes number
     */
    public int getNumRandomAttr() {
        return numRandomAttr;
    }

    /**
     * Sets the value of minimum objects per leaf.
     *
     * @param minObj the value of minimum objects per leaf
     * @throws IllegalArgumentException if the value of minimum objects per leaf is less than zero
     */
    public void setMinObj(int minObj) {
        eca.core.Assert.notNegative(minObj);
        this.minObj = minObj;
    }

    /**
     * Sets the value of maximum tree depth.
     *
     * @param maxDepth the value of maximum tree depth.
     * @throws IllegalArgumentException if the value of maximum tree depth is less than zero
     */
    public void setMaxDepth(int maxDepth) {
        eca.core.Assert.notNegative(maxDepth);
        this.maxDepth = maxDepth;
    }

    /**
     * Returns the value of minimum objects per leaf.
     *
     * @return the value of minimum objects per leaf
     */
    public int getMinObj() {
        return minObj;
    }

    /**
     * Returns the value of maximum tree depth.
     *
     * @return the value of maximum tree depth
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Return decision tree type.
     *
     * @return {@link DecisionTreeType} object
     */
    public DecisionTreeType getDecisionTreeType() {
        return decisionTreeType;
    }

    /**
     * Sets decision tree type.
     *
     * @param decisionTreeType {@link DecisionTreeType} object
     */
    public void setDecisionTreeType(DecisionTreeType decisionTreeType) {
        Assert.notNull(decisionTreeType, "Decision tree type is not specified!");
        this.decisionTreeType = decisionTreeType;
    }

    @Override
    public IterativeBuilder getIterativeBuilder(Instances data) throws Exception {
        return new ForestBuilder(data);
    }

    @Override
    public String[] getOptions() {
        List<String> options = getListOptions();
        return options.toArray(new String[options.size()]);
    }

    @Override
    public List<String> getListOptions() {
        List<String> optionsList = new ArrayList<>();
        optionsList.addAll(Arrays.asList(ForestsDictionary.NUM_TREES, String.valueOf(getIterationsNum()),
                DecisionTreeDictionary.MIN_NUM_OBJECTS_IN_LEAF, String.valueOf(minObj),
                DecisionTreeDictionary.MAX_DEPTH, String.valueOf(maxDepth),
                DecisionTreeDictionary.NUM_RANDOM_ATTRS, String.valueOf(numRandomAttr),
                ForestsDictionary.DECISION_TREE_ALGORITHM, decisionTreeType.name()));
        return optionsList;
    }

    @Override
    protected void initialize() {
        votes = new MajorityVoting(new Aggregator(this));
    }

    protected class ForestBuilder extends AbstractBuilder {

        Sampler sampler = new Sampler();

        ForestBuilder(Instances dataSet) throws Exception {
            super(dataSet);
            if (numRandomAttr > filteredData.numAttributes() - 1) {
                throw new IllegalArgumentException(String.format("Wrong value of numRandomAttr: %d", numRandomAttr));
            }
        }

        DecisionTreeClassifier createDecisionTree(Instances sample) {
            DecisionTreeClassifier treeClassifier = getDecisionTreeType().handle(decisionTreeBuilder);
            treeClassifier.setRandomTree(true);
            treeClassifier.setNumRandomAttr(getNumRandomAttr());
            treeClassifier.setMinObj(getMinObj());
            treeClassifier.setMaxDepth(getMaxDepth());
            return treeClassifier;
        }

        @Override
        public int next() throws Exception {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Instances bootstrap = sampler.bootstrap(filteredData);
            DecisionTreeClassifier treeClassifier = createDecisionTree(bootstrap);
            treeClassifier.setUseBinarySplits(true);
            treeClassifier.buildClassifier(bootstrap);
            classifiers.add(treeClassifier);
            return ++index;
        }

    }

}
