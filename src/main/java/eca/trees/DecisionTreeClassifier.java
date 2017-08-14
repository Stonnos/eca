/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.trees;

import eca.core.AttributesEnumeration;
import eca.core.InstancesHandler;
import eca.core.PermutationsSearch;
import eca.filter.MissingValuesFilter;
import eca.trees.rules.AbstractRule;
import eca.trees.rules.BinaryRule;
import eca.trees.rules.NominalRule;
import eca.trees.rules.NumericRule;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Abstract class for generating decision tree model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set minimum number of instances per leaf. (Default: 2) <p>
 * <p>
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 * <p>
 * Use random tree. <p>
 * <p>
 * Set number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 *
 * @author Рома
 */
public abstract class DecisionTreeClassifier extends AbstractClassifier
        implements InstancesHandler {

    /**
     * Initial training set
     **/
    protected Instances data;

    /**
     * Tree root
     **/
    protected TreeNode root;

    /**
     * Number of tree nodes
     **/
    protected int numNodes;

    /**
     * Number of tree leaves
     **/
    protected int numLeaves;

    /**
     * Number of instances per leaf
     **/
    protected int minObj = 2;

    /**
     * Maximum tree depth
     **/
    protected int maxDepth;

    /**
     * Number of random attributes at each split
     **/
    protected int numRandomAttr;

    /**
     * Random tree?
     **/
    protected boolean isRandom;

    /**
     * Tree depth
     **/
    protected int depth;

    /**
     * Node split algorithm
     **/
    protected SplitAlgorithm splitAlgorithm;

    protected double[] probabilities;

    protected double currentMeasure;

    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Tree node model.
     */
    protected static class TreeNode implements java.io.Serializable {

        Instances objects;
        TreeNode[] child;
        TreeNode parent;
        boolean leaf;
        double classValue;
        AbstractRule rule;
        int depth;
        int index;

        TreeNode(TreeNode parent) {
            this.parent = parent;
            objects = new Instances(parent.objects, parent.objectsNum() / parent.childrenNum());
        }

        TreeNode(Instances data) {
            objects = new Instances(data);
        }

        TreeNode getChild(Instance obj) {
            return child[rule.getChild(obj)];
        }

        AbstractRule getRule() {
            return rule;
        }

        TreeNode getChild(int i) {
            return child[i];
        }

        TreeNode lastChild() {
            return getChild(childrenNum() - 1);
        }

        double classValue() {
            return classValue;
        }

        void setClassValue(double classValue) {
            this.classValue = classValue;
        }

        int getDepth() {
            return depth;
        }

        void setDepth(int depth) {
            this.depth = depth;
        }

        int index() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        void setChild(int i, TreeNode x) {
            child[i] = x;
        }

        int childrenNum() {
            return child.length;
        }

        boolean isLeaf() {
            return leaf;
        }

        boolean addObject(Instance obj) {
            return objects.add(obj);
        }

        int objectsNum() {
            return objects.numInstances();
        }

        boolean isEmpty() {
            return objects.isEmpty();
        }

        Instances objects() {
            return objects;
        }

        TreeNode[] children() {
            return child;
        }

        void setLeaf() {
            leaf = true;
            child = null;
            rule = null;
        }

        boolean isSplit(int bound) {
            for (TreeNode c : child) {
                if (c.objectsNum() < bound) {
                    return false;
                }
            }
            return true;
        }

        boolean isMinObj(int minObj) {
            return objectsNum() <= minObj;
        }

        boolean isMaxDepth(int maxDepth) {
            return depth == maxDepth;
        }

    } //End of class TreeNode


    /**
     * Class for storage node split results.
     */
    protected static class SplitDescriptor {

        TreeNode[] child;

        AbstractRule rule;

    }

    /**
     * Split algorithm interface.
     */
    protected interface SplitAlgorithm extends Serializable {

        double getMeasure(TreeNode x);

        boolean isBetterSplit(double measure);

    }

    protected final void processNumericSplit(Attribute a,
                                             TreeNode x,
                                             SplitAlgorithm splitAlgorithm,
                                             SplitDescriptor split) {
        x.objects().sort(a);
        NumericRule rule = new NumericRule(a);
        double optThreshold = 0.0;
        x.rule = rule;
        for (int i = 0; i < x.objectsNum() - 1; i++) {
            rule.setMeanValue((x.objects().instance(i).value(a)
                    + x.objects().instance(i + 1).value(a)) / 2);
            createChildren(x, 2);
            double measure = splitAlgorithm.getMeasure(x);
            if (splitAlgorithm.isBetterSplit(measure)) {
                currentMeasure = measure;
                split.child = x.child;
                optThreshold = rule.getMeanValue();
                split.rule = x.rule;
            }
        }
        rule.setMeanValue(optThreshold);
    }

    protected final void processNominalSplit(Attribute a,
                                             TreeNode x,
                                             SplitAlgorithm splitAlgorithm,
                                             SplitDescriptor split) {
        x.rule = new NominalRule(a);
        createChildren(x, a.numValues());
        double measure = splitAlgorithm.getMeasure(x);
        if (splitAlgorithm.isBetterSplit(measure)) {
            currentMeasure = measure;
            split.child = x.child;
            split.rule = x.rule;
        }
    }

    protected final void processBinarySplit(Attribute a,
                                            TreeNode x,
                                            SplitAlgorithm splitAlgorithm,
                                            SplitDescriptor split) {
        int[] values = new int[a.numValues()];
        int[] optValues = null;
        BinaryRule rule = new BinaryRule(a, values);
        x.rule = rule;
        for (int i = 1; i <= a.numValues() / 2; i++) {

            for (int j = 0; j < values.length; j++) {
                values[j] = j < i ? 1 : 0;
            }

            PermutationsSearch permutationsSearch = new PermutationsSearch();
            permutationsSearch.setValues(values);
            while (permutationsSearch.nextPermutation()) {
                createChildren(x, 2);
                double measure = splitAlgorithm.getMeasure(x);
                if (splitAlgorithm.isBetterSplit(measure)) {
                    currentMeasure = measure;
                    split.child = x.child;
                    optValues = Arrays.copyOf(values, values.length);
                    split.rule = x.rule;
                }
            }
        }

        rule.setValues(optValues);
    }

    /**
     * Returns the value of random tree.
     *
     * @return the value of random tree
     */
    public final boolean isRandomTree() {
        return isRandom;
    }

    /**
     * Sets the value of random tree.
     *
     * @param isRandom the value of random tree
     */
    public final void setRandomTree(boolean isRandom) {
        this.isRandom = isRandom;
    }

    /**
     * Sets the value of minimum objects per leaf.
     *
     * @param minObj the value of minimum objects per leaf
     * @throws IllegalArgumentException if the value of minimum objects per leaf is less than zero
     */
    public final void setMinObj(int minObj) {
        checkForNegative(minObj);
        this.minObj = minObj;
    }

    /**
     * Returns the value of minimum objects per leaf.
     *
     * @return the value of minimum objects per leaf
     */
    public final int getMinObj() {
        return minObj;
    }

    /**
     * Returns the value of maximum tree depth.
     *
     * @return the value of maximum tree depth
     */
    public final int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Sets the value of maximum tree depth.
     *
     * @param maxDepth the value of maximum tree depth.
     * @throws IllegalArgumentException if the value of maximum tree depth is less than zero
     */
    public void setMaxDepth(int maxDepth) {
        checkForNegative(maxDepth);
        this.maxDepth = maxDepth;
    }

    /**
     * Returns leaves number.
     *
     * @return leaves number
     */
    public final int numLeaves() {
        return numLeaves;
    }

    /**
     * Returns nodes number
     *
     * @return nodes number
     */
    public final int numNodes() {
        return numNodes;
    }

    /**
     * Returns tree depth.
     *
     * @return tree depth
     */
    public final int depth() {
        return depth;
    }

    /**
     * Returns the value of random attributes number.
     *
     * @return the value of random attributes number
     */
    public final int numRandomAttr() {
        return numRandomAttr;
    }

    /**
     * Sets the value of random attributes number
     *
     * @param numRandomAttr the value of random attributes number
     * @throws IllegalArgumentException if the value of random attributes number is less than zero
     */
    public final void setNumRandomAttr(int numRandomAttr) {
        checkForNegative(numRandomAttr);
        this.numRandomAttr = numRandomAttr;
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public double[] distributionForInstance(Instance obj) {
        Instance o = filter.filterInstance(obj);
        TreeNode x = root;
        while (!x.isLeaf()) {
            x = x.getChild(o);
        }
        probabilities(x);
        return Arrays.copyOf(probabilities, probabilities.length);
    }

    @Override
    public String[] getOptions() {
        String[] options = {"Минимальное число объектов в листе:", String.valueOf(minObj),
                "Максиальная глубина дерева:", String.valueOf(maxDepth),
                "Случайное дерево:", String.valueOf(isRandom),
                "Число случайных атрибутов:", String.valueOf(numRandomAttr)};
        return options;
    }

    @Override
    public double classifyInstance(Instance obj) {
        Instance o = filter.filterInstance(obj);
        TreeNode x = root;
        while (!x.isLeaf()) {
            x = x.getChild(o);
        }
        return x.classValue();
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        this.data = data;
        if (isRandomTree() && numRandomAttr > data.numAttributes() - 1) {
            numRandomAttr = 0;
        }
        probabilities = new double[data.numClasses()];
        root = new TreeNode(filter.filterInstances(data));
        root.setDepth(1);
        root.setClassValue(classValue(root));
        numNodes++;
        createDecisionTree(root);
    }

    private void checkForNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Negative value: "
                    + String.valueOf(value));
        }
    }

    private void createDecisionTree(TreeNode x) {
        doSplit(x);
        if (!x.isLeaf()) {
            for (TreeNode c : x.children()) {
                createDecisionTree(c);
            }
        }
        depth = Integer.max(depth, x.depth);
    }

    protected boolean isSplit(TreeNode x) {
        return x.isSplit(minObj);
    }

    protected final void doSplit(TreeNode x) {
        if (x.isMaxDepth(maxDepth) || x.isMinObj(minObj) || isClean(x)) {
            x.setLeaf();
            numLeaves++;
        } else {
            SplitDescriptor split = createOptSplit(x);
            x.child = split.child;
            if (isSplit(x)) {
                x.rule = split.rule;
                numNodes += x.childrenNum();
                setClasses(x);
            } else {
                x.setLeaf();
                numLeaves++;
            }
        }
    }

    protected abstract SplitDescriptor createOptSplit(TreeNode x);

    protected final void probabilities(TreeNode x) {
        for (int k = 0; k < probabilities.length; k++) {
            probabilities[k] = 0;
        }
        if (!x.objects().isEmpty()) {
            for (int i = 0; i < x.objects().numInstances(); i++) {
                probabilities[(int) x.objects().instance(i).classValue()]++;
            }
            Utils.normalize(probabilities);
        }
    }

    protected final double classValue(TreeNode x) {
        double classValue = 0.0;
        double max = -Double.MAX_VALUE;
        probabilities(x);
        for (int k = 0; k < probabilities.length; k++) {
            if (probabilities[k] > max) {
                max = probabilities[k];
                classValue = k;
            }
        }

        for (int i = 0; i < probabilities.length - 1; i++) {
            if (probabilities[i] != probabilities[i + 1]) {
                return classValue;
            }
        }

        return x.parent != null ? x.parent.classValue() : classValue;
    }

    protected final double nodeError(TreeNode x) {
        int count = 0;
        for (int i = 0; i < x.objects().numInstances(); i++) {
            if (x.objects().instance(i).classValue() != x.classValue()) {
                count++;
            }
        }
        return (double) count / x.objectsNum();
    }

    protected final boolean isClean(TreeNode x) {
        for (int i = 0; i < x.objectsNum() - 1; i++) {
            if (x.objects().instance(i).classValue()
                    != x.objects().instance(i + 1).classValue()) {
                return false;
            }
        }
        return true;
    }

    protected final void setChildren(TreeNode x) {
        for (int i = 0; i < x.childrenNum(); i++) {
            x.setChild(i, new TreeNode(x));
            x.getChild(i).setDepth(x.getDepth() + 1);
        }
    }

    protected final void setClasses(TreeNode x) {
        for (TreeNode c : x.children()) {
            c.setClassValue(classValue(c));
        }
    }

    protected final void addObjects(TreeNode x) {
        for (int i = 0; i < x.objects().numInstances(); i++) {
            Instance obj = x.objects().instance(i);
            int k = x.getRule().getChild(obj);
            x.getChild(k).addObject(obj);
        }
    }

    protected final void createChildren(TreeNode x, int childrenNum) {
        x.child = new TreeNode[childrenNum];
        setChildren(x);
        addObjects(x);
    }

    protected final Enumeration<Attribute> attributes() {
        if (isRandomTree()) {
            if (numRandomAttr == 0 || numRandomAttr == data.numAttributes() - 1) {
                return data.enumerateAttributes();
            } else {
                return new AttributesEnumeration(data, numRandomAttr);
            }
        } else {
            return data.enumerateAttributes();
        }
    }

} //End of class DecisionTreeClassifier
