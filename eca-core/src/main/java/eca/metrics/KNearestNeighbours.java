/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics;

import eca.core.DecimalFormatHandler;
import eca.core.FilterHandler;
import eca.core.InstancesHandler;
import eca.core.MinMaxNormalizer;
import eca.filter.MissingValuesFilter;
import eca.metrics.distances.Distance;
import eca.metrics.distances.EuclidDistance;
import eca.metrics.distances.InstanceDistance;
import eca.text.NumericFormatFactory;
import eca.util.Utils;
import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Objects;

/**
 * Class for generating k - nearest neighbours model. <p>
 * <p>
 * Valid options are: <p>
 * <p>
 * Set nearest neighbour weight (Default: 1) <p>
 * <p>
 * Set number of nearest neighbours (Default: 10) <p>
 * <p>
 * Set distance function (Default: {@link EuclidDistance}) <p>
 *
 * @author Roman Batygin
 */
public class KNearestNeighbours extends AbstractClassifier
        implements InstancesHandler, DecimalFormatHandler, FilterHandler {

    public static final double MIN_WEIGHT = 0.5;

    public static final double MAX_WEIGHT = 1.0;

    public static final int MIN_NEIGHBOURS_NUMBER = 1;

    /**
     * Initial training set
     **/
    private Instances data;

    /**
     * Normalized training set
     **/
    private Instances normalizedData;

    /**
     * Number of nearest neighbours
     **/
    private int numNeighbours = 10;

    /**
     * Nearest neighbour weight
     **/
    private double weight = 1.0;

    /**
     * Distance function
     **/
    private Distance metric;

    /**
     * Decimal format.
     */
    private DecimalFormat decimalFormat = NumericFormatFactory.getInstance();

    private MinMaxNormalizer normalizer;
    private InstanceDistance[] distances;
    private final NominalToBinary nominalToBinary = new NominalToBinary();
    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Creates <tt>KNearestNeighbours</tt> object.
     *
     * @param metric distance function
     */
    public KNearestNeighbours(Distance metric) {
        this.metric = metric;
    }

    /**
     * Creates <tt>KNearestNeighbours</tt> object.
     */
    public KNearestNeighbours() {
        this(new EuclidDistance());
    }

    @Override
    public Instances getData() {
        return data;
    }

    @Override
    public MissingValuesFilter getFilter() {
        return filter;
    }

    /**
     * Returns decimal format.
     *
     * @return {@link DecimalFormat} object
     */
    @Override
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public String[] getOptions() {
        return new String[]{KNNDictionary.NEIGHBOURS_NUM, String.valueOf(numNeighbours),
                KNNDictionary.NEIGHBOUR_WEIGHT, getDecimalFormat().format(weight),
                KNNDictionary.DISTANCE_FUNCTION, metric.getDistanceType().getDescription()};
    }

    /**
     * Returns the number of nearest neighbours.
     *
     * @return the number of nearest neighbours
     */
    public int getNumNeighbours() {
        return numNeighbours;
    }

    /**
     * Returns the value of nearest neighbour weight.
     *
     * @return the value of nearest neighbour weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the number of nearest neighbours.
     *
     * @param numNeighbours the number of nearest neighbours
     * @throws IllegalArgumentException if the number of nearest neighbours is less than 1
     */
    public void setNumNeighbours(int numNeighbours) {
        if (numNeighbours < MIN_NEIGHBOURS_NUMBER) {
            throw new IllegalArgumentException(
                    String.format(KNNDictionary.BAD_NEIGHBOURS_NUM_ERROR_FORMAT, MIN_NEIGHBOURS_NUMBER));
        }
        this.numNeighbours = numNeighbours;
    }

    /**
     * Sets the value of nearest neighbour weight.
     *
     * @param weight the value of nearest neighbour weight
     * @throws IllegalArgumentException if the value of nearest neighbour weight is less
     *                                  than {@value MIN_WEIGHT} or greater than {@value MAX_WEIGHT}
     */
    public void setWeight(double weight) {
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException(
                    String.format(KNNDictionary.BAD_WEIGHT_ERROR_FORMAT, MIN_WEIGHT, MAX_WEIGHT));
        }
        this.weight = weight;
    }

    /**
     * Returns the distance function object.
     *
     * @return the distance function object
     */
    public Distance getDistance() {
        return metric;
    }

    /**
     * Sets the distance function object.
     *
     * @param metric the distance function object
     */
    public void setDistance(Distance metric) {
        Objects.requireNonNull(metric, "Distance is not specified!");
        this.metric = metric;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        if (numNeighbours > data.numInstances()) {
            setNumNeighbours(data.numInstances());
        }
        this.data = data;
        Instances set = filter.filterInstances(data);
        nominalToBinary.setInputFormat(set);
        set = Filter.useFilter(set, nominalToBinary);
        normalizer = new MinMaxNormalizer(set);
        normalizedData = normalizer.normalizeInstances();
        distances = new InstanceDistance[normalizedData().numInstances()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = new InstanceDistance();
        }
    }

    @Override
    public double[] distributionForInstance(Instance obj) {
        double[] weights = getWeights(obj);
        Utils.normalize(weights);
        return weights;
    }

    @Override
    public double classifyInstance(Instance obj) {
        double[] weights = getWeights(obj);
        double classValue = 0.0;
        double maxVal = -Double.MAX_VALUE;

        for (int i = 0; i < weights.length; i++) {
            if (weights[i] > maxVal) {
                maxVal = weights[i];
                classValue = i;
            }
        }

        return classValue;
    }

    private Instances normalizedData() {
        return normalizedData;
    }

    private double[] getWeights(Instance obj) {
        Instance instance = filter.filterInstance(obj);
        nominalToBinary.input(instance);
        instance = nominalToBinary.output();
        instance = normalizer.normalizeInstance(instance);

        for (int i = 0; i < distances.length; i++) {
            InstanceDistance ins = distances[i];
            ins.setId(i);
            ins.setDistance(metric.distance(instance, normalizedData.instance(i)));
        }

        Arrays.parallelSort(distances);

        double[] weights = new double[instance.numClasses()];
        for (int i = 0; i < numNeighbours; i++) {
            int classIndex = (int) normalizedData.instance(distances[i].getId()).classValue();
            weights[classIndex] += Math.pow(weight, i);
        }

        return weights;
    }

}
