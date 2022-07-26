package eca.trees;

import eca.core.Assert;
import eca.core.FilterHandler;
import eca.core.InstancesHandler;
import eca.core.ListOptionsHandler;
import eca.core.PermutationsSearcher;
import eca.core.RandomAttributesEnumeration;
import eca.filter.MissingValuesFilter;
import eca.generators.NumberGenerator;
import eca.trees.rules.AbstractRule;
import eca.trees.rules.BinaryRule;
import eca.trees.rules.NominalRule;
import eca.trees.rules.NumericRule;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Randomizable;
import weka.core.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Abstract class for generating decision tree model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set minimum number of instances per leaf. (Default: 2) <p>
 * <p>
 * Set maximum tree depth. (Default: 0 (denotes infinity)) <p>
 * <p>
 * Use random tree. (Default: <tt>false</tt>) <p>
 * <p>
 * Set the number of random attributes at each split. (Default: 0 (denotes all attributes)) <p>
 * <p>
 * Use binary splits. (Default: <tt>true</tt>) <p>
 * <p>
 * Use random splits of each attribute (Default: <tt>false</tt>) <p>
 * <p>
 * Set the number of attribute random splits (Default: 1) <p>
 *
 * @author Roman Batygin
 */
public abstract class DecisionTreeClassifier extends AbstractClassifier
        implements InstancesHandler, ListOptionsHandler, Randomizable, FilterHandler {

    public static final int MIN_RANDOM_SPLITS = 1;

    /**
     * Initial training set
     **/
    private Instances data;

    /**
     * Filtered data
     */
    private Instances filteredData;

    /**
     * Tree root
     **/
    protected TreeNode root;

    /**
     * Number of tree nodes
     **/
    private int numNodes;

    /**
     * Number of tree leaves
     **/
    private int numLeaves;

    /**
     * Number of instances per leaf
     **/
    private int minObj = 2;

    /**
     * Maximum tree depth
     **/
    private int maxDepth;

    /**
     * Number of random attributes at each split
     **/
    private int numRandomAttr;

    /**
     * Random tree?
     **/
    private boolean isRandom;

    /**
     * Use binary splits?
     **/
    private boolean useBinarySplits;

    /**
     * Use random splits?
     */
    private boolean useRandomSplits;

    /**
     * Number of random splits of each attribute
     */
    private int numRandomSplits = 1;

    /**
     * Seed value for random generator
     */
    private int seed;

    /**
     * Tree depth
     **/
    private int depth;

    /**
     * Node split algorithm
     **/
    protected SplitAlgorithm splitAlgorithm;

    protected double[] probabilities;
    protected double[][] probabilitiesMatrix;
    protected int[] childrenSizes;

    protected Random random;

    private final MissingValuesFilter filter = new MissingValuesFilter();

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
        Assert.notNegative(minObj, "Negative value for min obj. per leaf!");
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
        Assert.notNegative(maxDepth, "Negative value for max depth!");
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
    public final int getNumRandomAttr() {
        return numRandomAttr;
    }

    /**
     * Sets the value of binary splits.
     *
     * @param flag the value of binary splits
     */
    public void setUseBinarySplits(boolean flag) {
        this.useBinarySplits = flag;
    }

    /**
     * Returns the value of binary splits.
     *
     * @return the value of binary splits
     */
    public boolean getUseBinarySplits() {
        return useBinarySplits;
    }

    /**
     * Returns the value of random splits.
     *
     * @return the value of random splits
     */
    public boolean isUseRandomSplits() {
        return useRandomSplits;
    }

    /**
     * Sets the value of random splits.
     *
     * @param useRandomSplits the value of random splits
     */
    public void setUseRandomSplits(boolean useRandomSplits) {
        this.useRandomSplits = useRandomSplits;
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
     * @throws IllegalArgumentException if the value the number of random splits is less than {@value MIN_RANDOM_SPLITS}
     */
    public void setNumRandomSplits(int numRandomSplits) {
        if (numRandomSplits < MIN_RANDOM_SPLITS) {
            throw new IllegalArgumentException(
                    String.format(DecisionTreeDictionary.BAD_RANDOM_ATTRS_NUMBER_ERROR_TEXT, MIN_RANDOM_SPLITS));
        }
        this.numRandomSplits = numRandomSplits;
    }

    /**
     * Sets the value of random attributes number
     *
     * @param numRandomAttr the value of random attributes number
     * @throws IllegalArgumentException if the value of random attributes number is less than zero
     */
    public final void setNumRandomAttr(int numRandomAttr) {
        Assert.notNegative(numRandomAttr, "Negative value for num. random attributes!");
        this.numRandomAttr = numRandomAttr;
    }

    @Override
    public int getSeed() {
        return seed;
    }

    @Override
    public void setSeed(int seed) {
        this.seed = seed;
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public MissingValuesFilter getFilter() {
        return filter;
    }

    @Override
    public double[] distributionForInstance(Instance obj) {
        Instance o = filter.filterInstance(obj);
        TreeNode x = root;
        while (!x.isLeaf()) {
            x = x.getChild(o);
        }
        calculateProbabilities(x);
        return Arrays.copyOf(probabilities, probabilities.length);
    }

    @Override
    public String[] getOptions() {
        List<String> options = getListOptions();
        return options.toArray(new String[options.size()]);
    }

    @Override
    public List<String> getListOptions() {
        List<String> options = new ArrayList<>();
        options.add(DecisionTreeDictionary.MIN_NUM_OBJECTS_IN_LEAF);
        options.add(String.valueOf(minObj));
        options.add(DecisionTreeDictionary.MAX_DEPTH);
        options.add(String.valueOf(maxDepth));
        options.add(DecisionTreeDictionary.RANDOM_TREE);
        options.add(String.valueOf(isRandom));

        if (isRandomTree()) {
            options.add(DecisionTreeDictionary.NUM_RANDOM_ATTRS);
            options.add(String.valueOf(numRandomAttr));
        }

        options.add(DecisionTreeDictionary.BINARY_TREE);
        options.add(String.valueOf(getUseBinarySplits()));
        options.add(DecisionTreeDictionary.RANDOM_SPLITS);
        options.add(String.valueOf(isUseRandomSplits()));

        if (isUseRandomSplits()) {
            options.add(DecisionTreeDictionary.NUM_RANDOM_SPLITS);
            options.add(String.valueOf(numRandomSplits));
        }
        options.add(DecisionTreeDictionary.SEED);
        options.add(String.valueOf(seed));

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
        if (isRandomTree() && getNumRandomAttr() > data.numAttributes() - 1) {
            setNumRandomAttr(0);
        }
        if (isUseRandomSplits()) {
            setUseBinarySplits(true);
        }
        probabilities = new double[data.numClasses()];
        random = new Random(seed);
        filteredData = filter.filterInstances(data);
        root = new TreeNode(filteredData);
        root.setDepth(1);
        root.setClassValue(classValue(root));
        numNodes++;
        createDecisionTree(root);
    }

    /**
     * Tree node model.
     */
    protected static class TreeNode implements Serializable {

        List<Integer> objects = newArrayList();
        TreeNode[] child;
        TreeNode parent;
        boolean leaf;
        double classValue;
        AbstractRule rule;
        int depth;
        int index;

        TreeNode(Instances data) {
            IntStream.range(0, data.numInstances()).forEach(i -> objects.add(i));
        }

        TreeNode(TreeNode parent) {
            this.parent = parent;
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

        void addObject(Integer objIndex) {
            objects.add(objIndex);
        }

        int objectsNum() {
            return objects.size();
        }

        List<Integer> objects() {
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

        TreeNode node;

        AbstractRule rule;

        double currentMeasure;

        SplitDescriptor(TreeNode node, double currentMeasure) {
            this.node = node;
            this.currentMeasure = currentMeasure;
        }

        TreeNode getNode() {
            return node;
        }

        void setNode(TreeNode node) {
            this.node = node;
        }

        AbstractRule getRule() {
            return rule;
        }

        void setRule(AbstractRule rule) {
            this.rule = rule;
        }

        double getCurrentMeasure() {
            return currentMeasure;
        }

        void setCurrentMeasure(double currentMeasure) {
            this.currentMeasure = currentMeasure;
        }

        void setParams(double currentMeasure, AbstractRule rule) {
            setCurrentMeasure(currentMeasure);
            setRule(rule);
        }
    }

    /**
     * Split algorithm interface.
     */
    protected interface SplitAlgorithm extends Serializable {

        double getMeasure(TreeNode x);

        double getMaxMeasure();

        boolean isBetterSplit(double currentMeasure, double measure);

    }

    private int getNumChildren(Attribute attribute) {
        return attribute.isNumeric() || getUseBinarySplits() ? 2 : attribute.numValues();
    }

    private void initializeProbabilityArrays(Attribute attribute) {
        int numChildren = getNumChildren(attribute);
        if (getUseBinarySplits()) {
            if (probabilitiesMatrix == null) {
                probabilitiesMatrix = new double[numChildren][data.numClasses()];
                childrenSizes = new int[numChildren];
            } else {
                for (double[] probabilitiesArray : probabilitiesMatrix) {
                    Arrays.fill(probabilitiesArray, 0);
                }
                Arrays.fill(childrenSizes, 0);
            }
        } else {
            probabilitiesMatrix = new double[numChildren][data.numClasses()];
            childrenSizes = new int[numChildren];
        }
    }

    protected final void calculateProbabilities(TreeNode x, boolean normalize) {
        initializeProbabilityArrays(x.getRule().attribute());
        x.objects().forEach(objIndex -> {
            Instance obj = filteredData.instance(objIndex);
            int k = x.getRule().getChild(obj);
            probabilitiesMatrix[k][(int) obj.classValue()]++;
            childrenSizes[k]++;
        });
        if (normalize) {
            for (double[] probabilitiesArray : probabilitiesMatrix) {
                eca.util.Utils.normalize(probabilitiesArray);
            }
        }
    }

    private List<Double> getSortedNumericAttributeVector(TreeNode x, Attribute a) {
        return x.objects()
                .stream()
                .map(i -> filteredData.instance(i).value(a))
                .sorted()
                .collect(Collectors.toList());
    }

    private void processNumericSplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split) {
        TreeNode x = split.getNode();
        List<Double> values = getSortedNumericAttributeVector(x, a);
        NumericRule rule = new NumericRule(a);
        double optThreshold = 0.0;
        x.rule = rule;
        for (int i = 0; i < values.size() - 1; i++) {
            rule.setMeanValue((values.get(i) + values.get(i + 1)) / 2);
            double measure = splitAlgorithm.getMeasure(x);
            if (splitAlgorithm.isBetterSplit(split.getCurrentMeasure(), measure)) {
                split.setParams(measure, x.getRule());
                optThreshold = rule.getMeanValue();
            }
        }
        rule.setMeanValue(optThreshold);
    }

    private void processNominalSplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split) {
        TreeNode x = split.getNode();
        x.rule = new NominalRule(a);
        double measure = splitAlgorithm.getMeasure(x);
        if (splitAlgorithm.isBetterSplit(split.getCurrentMeasure(), measure)) {
            split.setParams(measure, x.getRule());
        }
    }

    private void processBinarySplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split) {
        TreeNode x = split.getNode();
        int[] values = new int[a.numValues()];
        int[] optValues = null;
        BinaryRule rule = new BinaryRule(a, values);
        x.rule = rule;
        for (int i = 1; i <= a.numValues() / 2; i++) {
            for (int j = 0; j < values.length; j++) {
                values[j] = j < i ? 1 : 0;
            }
            int[] currentOptValues = findOptimalBinarySplit(splitAlgorithm, split, values);
            if (currentOptValues != null) {
                optValues = currentOptValues;
            }
        }
        rule.setCodes(optValues);
    }

    private void processRandomSplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split, int k) {
        if (a.isNumeric()) {
            processNumericRandomSplit(a, splitAlgorithm, split, k);
        } else {
            processNominalRandomSplit(a, splitAlgorithm, split, k);
        }
    }

    private double getMinValue(TreeNode x, Attribute a) {
        return x.objects()
                .stream()
                .mapToDouble(i -> filteredData.instance(i).value(a))
                .min()
                .getAsDouble();
    }

    private double getMaxValue(TreeNode x, Attribute a) {
        return x.objects()
                .stream()
                .mapToDouble(i -> filteredData.instance(i).value(a))
                .max()
                .getAsDouble();
    }

    private void processNumericRandomSplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split, int k) {
        TreeNode x = split.getNode();
        double minAttrValue = getMinValue(x, a);
        double maxAttrValue = getMaxValue(x, a);
        NumericRule rule = new NumericRule(a);
        double optThreshold = 0.0;
        x.rule = rule;
        for (int i = 0; i < k; i++) {
            rule.setMeanValue(NumberGenerator.random(random, minAttrValue, maxAttrValue));
            double measure = splitAlgorithm.getMeasure(x);
            if (splitAlgorithm.isBetterSplit(split.getCurrentMeasure(), measure)) {
                split.setParams(measure, x.getRule());
                optThreshold = rule.getMeanValue();
            }
        }
        rule.setMeanValue(optThreshold);
    }

    private void processNominalRandomSplit(Attribute a, SplitAlgorithm splitAlgorithm, SplitDescriptor split, int k) {
        TreeNode x = split.getNode();
        if (k < Math.pow(2, a.numValues() - 1.0) - 1) {
            int[] values = new int[a.numValues()];
            int[] optValues = null;
            BinaryRule rule = new BinaryRule(a, values);
            x.rule = rule;
            for (int i = 0; i < k; i++) {
                Arrays.fill(values, 0);
                int subSetSize = random.nextInt(a.numValues() - 1) + 1;
                while (subSetSize != 0) {
                    int randomIndex = random.nextInt(values.length);
                    if (values[randomIndex] == 0) {
                        values[randomIndex] = 1;
                        subSetSize--;
                    }
                }
                int[] currentOptValues = findOptimalBinarySplit(splitAlgorithm, split, values);
                if (currentOptValues != null) {
                    optValues = currentOptValues;
                }
            }
            rule.setCodes(optValues);
        } else {
            processBinarySplit(a, splitAlgorithm, split);
        }
    }

    private int[] findOptimalBinarySplit(SplitAlgorithm splitAlgorithm, SplitDescriptor splitDescriptor, int[] values) {
        int[] optValues = null;
        TreeNode x = splitDescriptor.getNode();
        PermutationsSearcher permutationsSearch = new PermutationsSearcher();
        permutationsSearch.setValues(values);
        while (permutationsSearch.nextPermutation()) {
            double measure = splitAlgorithm.getMeasure(x);
            if (splitAlgorithm.isBetterSplit(splitDescriptor.getCurrentMeasure(), measure)) {
                splitDescriptor.setParams(measure, x.getRule());
                optValues = Arrays.copyOf(values, values.length);
            }
        }
        return optValues;
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

    protected boolean isSplit(SplitDescriptor splitDescriptor) {
        return splitDescriptor.getNode().isSplit(minObj);
    }

    private void doSplit(TreeNode x) {
        if (x.isMaxDepth(maxDepth) || x.isMinObj(minObj) || isClean(x)) {
            x.setLeaf();
            numLeaves++;
        } else {
            SplitDescriptor split = createOptSplit(x);
            x.rule = split.rule;
            createChildren(x, getNumChildren(x.getRule().attribute()));
            if (isSplit(split)) {
                numNodes += x.childrenNum();
                setClasses(x);
            } else {
                x.setLeaf();
                numLeaves++;
            }
        }
    }

    private SplitDescriptor createOptSplit(TreeNode x) {
        SplitDescriptor split = new SplitDescriptor(x, splitAlgorithm.getMaxMeasure());
        for (Enumeration<Attribute> e = attributes(); e.hasMoreElements(); ) {
            Attribute a = e.nextElement();
            if (isUseRandomSplits()) {
                processRandomSplit(a, splitAlgorithm, split, numRandomSplits);
            } else {
                if (a.isNumeric()) {
                    processNumericSplit(a, splitAlgorithm, split);
                } else {
                    if (getUseBinarySplits()) {
                        processBinarySplit(a, splitAlgorithm, split);
                    } else {
                        processNominalSplit(a, splitAlgorithm, split);
                    }
                }
            }
        }

        return split;
    }

    protected final void calculateProbabilities(TreeNode x) {
        Arrays.fill(probabilities, 0);
        if (!x.objects().isEmpty()) {
            x.objects().forEach(objIndex -> probabilities[(int) filteredData.instance(objIndex).classValue()]++);
            if (!Utils.eq(Utils.sum(probabilities), 0)) {
                Utils.normalize(probabilities);
            }
        }
    }

    private double classValue(TreeNode x) {
        double classValue = 0.0;
        double max = -Double.MAX_VALUE;
        calculateProbabilities(x);
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

    protected final double calculateNodeError(TreeNode x) {
        long count = x.objects()
                .stream()
                .filter(objIndex -> filteredData.instance(objIndex).classValue() != x.classValue())
                .count();
        return (double) count / x.objectsNum();
    }

    private boolean isClean(TreeNode x) {
        for (int i = 0; i < x.objectsNum() - 1; i++) {
            Instance first = filteredData.instance(x.objects().get(i));
            Instance second = filteredData.instance(x.objects().get(i + 1));
            if (first.classValue() != second.classValue()) {
                return false;
            }
        }
        return true;
    }

    private void setChildren(TreeNode x) {
        for (int i = 0; i < x.childrenNum(); i++) {
            x.setChild(i, new TreeNode(x));
            x.getChild(i).setDepth(x.getDepth() + 1);
        }
    }

    private void setClasses(TreeNode x) {
        for (TreeNode c : x.children()) {
            c.setClassValue(classValue(c));
        }
    }

    private void addObjects(TreeNode x) {
        x.objects().forEach(objIndex -> {
            Instance obj = filteredData.instance(objIndex);
            int k = x.getRule().getChild(obj);
            x.getChild(k).addObject(objIndex);
        });
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
                return new RandomAttributesEnumeration(data, numRandomAttr, random);
            }
        } else {
            return data.enumerateAttributes();
        }
    }

} //End of class DecisionTreeClassifier