/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eca.metrics;

import eca.core.converters.MinMaxNormalizer;
import eca.metrics.distances.Distance;
import eca.metrics.distances.EuclidDistance;
import eca.metrics.distances.InstanceDistance;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.AbstractClassifier;
import eca.filter.MissingValuesFilter;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NominalToBinary;
import eca.core.InstancesHandler;
import java.util.Arrays;

/**
 * Class for generating k - nearest neighbours model. <p>
 *
 * Valid options are: <p>
 *
 * Set nearest neighbour weight (Default: 1) <p>
 *
 * Set number of nearest neighbours (Default: 10) <p>
 *
 * Set distance function (Default: {@link EuclidDistance}) <p>
 *
 * @author Рома
 */
public class KNearestNeighbours extends AbstractClassifier
        implements InstancesHandler {

    public static final double MIN_WEIGHT = 0.5;

    public static final double MAX_WEIGHT = 1.0;

    /** Initial training set **/
    private Instances data;

    /** Normalized training set **/
    private Instances normalizedData;

    /** Number of nearest neighbours **/
    private int numNeighbours = 10;

    /** Nearest neighbour weight **/
    private double weight = 1.0;

    /** Distance function **/
    private Distance metric;

    private MinMaxNormalizer normalizer;
    private InstanceDistance[] distances;
    private final NominalToBinary ntbFilter = new NominalToBinary();
    private final MissingValuesFilter filter = new MissingValuesFilter();

    /**
     * Creates <tt>KNearestNeighbours</tt> object.
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
    public String[] getOptions() {
        String[] options = {"Число ближайших соседей:", String.valueOf(numNeighbours),
            "Вес ближайшего соседа:", String.valueOf(weight),
            "Функция расстояния:", metric.getClass().getSimpleName()};
        return options;
    }

    /**
     * Returns the number of nearest neighbours.
     * @return the number of nearest neighbours
     */
    public int getNumNeighbours() {
        return numNeighbours;
    }

    /**
     * Returns the value of nearest neighbour weight.
     * @return the value of nearest neighbour weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the number of nearest neighbours.
     * @param numNeighbours the number of nearest neighbours
     * @exception IllegalArgumentException if the number of nearest neighbours is less than 1
     */
    public void setNumNeighbours(int numNeighbours) {
        if (numNeighbours < 1) {
            throw new IllegalArgumentException("Чило ближайших соседей должно быть не менее 1!");
        }
        this.numNeighbours = numNeighbours;
    }

    /**
     * Sets the value of nearest neighbour weight.
     * @param weight the value of nearest neighbour weight
     * @exception IllegalArgumentException if the value of nearest neighbour weight is less
     * than {@value MIN_WEIGHT} or greater than {@value MAX_WEIGHT}
     */
    public void setWeight(double weight) {
        if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
            throw new IllegalArgumentException("Вес должен лежать в интервале от "
                    + String.valueOf(MIN_WEIGHT) + " до " + String.valueOf(MAX_WEIGHT) + "!");
        }
        this.weight = weight;
    }

    /**
     * Returns the distance function object.
     * @return the distance function object
     */
    public Distance distance() {
        return metric;
    }

    /**
     * Sets the distance function object.
     * @param metric the distance function object
     */
    public void setDistance(Distance metric) {
        this.metric = metric;
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        if (numNeighbours > data.numInstances()) {
            numNeighbours = data.numInstances();
        }
        this.data = data;
        Instances set = filter.filterInstances(data);
        ntbFilter.setInputFormat(set);
        set = Filter.useFilter(set, ntbFilter);
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
        if (Utils.eq(Utils.sum(weights), 0)) {
            return weights;
        } else {
            Utils.normalize(weights);
        }
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
        Instance o = filter.filterInstance(obj);
        ntbFilter.input(o);
        o = ntbFilter.output();
        o = normalizer.normalizeInstance(o);

        for (int i = 0; i < distances.length; i++) {
            InstanceDistance ins = distances[i];
            ins.setId(i);
            ins.setDistance(metric.distance(o, normalizedData.instance(i)));
        }

        Arrays.parallelSort(distances);

        double[] weights = new double[o.numClasses()];
        for (int i = 0; i < numNeighbours; i++) {
            int classIndex = (int) normalizedData.instance(distances[i].getId()).classValue();
            weights[classIndex] += Math.pow(weight, i);
        }

        return weights;
    }

}
